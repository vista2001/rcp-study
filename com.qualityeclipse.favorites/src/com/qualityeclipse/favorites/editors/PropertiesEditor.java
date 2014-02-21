package com.qualityeclipse.favorites.editors;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.qualityeclipse.favorites.FavoritesLog;
import com.qualityeclipse.favorites.views.AltClickCellEditListener;

public class PropertiesEditor extends MultiPageEditorPart {

	private TreeViewer treeViewer;
	private TextEditor textEditor;

	private TreeColumn keyColumn;
	private TreeColumn valueColumn;

	private PropertiesEditorContentProvider treeContentProvider;
	private PropertiesEditorLabelProvider treeLabelProvider;

	private boolean isPageModified;
	
	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	private IUndoContext undoContext;

	private final PropertyFileListener propertyFileListener = new PropertyFileListener() {

		@Override
		public void valueChanged(PropertyCategory category, PropertyEntry entry) {
			treeViewer.refresh(entry);
			treeModified();
		}

		@Override
		public void keyChanged(PropertyCategory category, PropertyEntry entry) {
			treeViewer.refresh(entry);
			treeModified();
		}

		@Override
		public void nameChanged(PropertyCategory category) {
			treeViewer.refresh(category);
			treeModified();
		}

		@Override
		public void entryAdded(PropertyCategory category, PropertyEntry entry) {
			treeViewer.refresh();
			treeModified();
		}

		@Override
		public void entryRemoved(PropertyCategory category, PropertyEntry entry) {
			treeViewer.refresh();
			treeModified();
		}

		@Override
		public void categoryRemoved(PropertyCategory category) {
			treeViewer.refresh();
			treeModified();
		}

		@Override
		public void categoryAdded(PropertyCategory category) {
			treeViewer.refresh();
			treeModified();
		}
	};

	@Override
	protected void createPages() {
		createPropertiesPage();
		createSourcePage();
		updateTitle();
		initTreeContent();
		initTreeEditors();
		createContextMenu();
		initKeyBindingContext();
		initUndoRedo();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (getActivePage() == 0 && isPageModified) {
			updateTextEditorFromTree();
		}else if(getActivePage() ==1 && isPageModified){
			updateTreeFromTextEditor();
		}
		isPageModified = false;
		textEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		if(getActivePage() ==0 && isPageModified){
			updateTextEditorFromTree();
		}else if(getActivePage() ==1 && isPageModified){
			updateTreeFromTextEditor();
		}
		textEditor.doSaveAs();
		setInput(textEditor.getEditorInput());
		updateTitle();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}

	@Override
	public void setFocus() {
		switch (getActivePage()) {
		case 0:
			treeViewer.getTree().setFocus();
			break;
		case 1:
			textEditor.setFocus();
			break;
		}
	}
	/**创建属性页面*/
	void createPropertiesPage() {
		Composite treeContainer = new Composite(getContainer(), SWT.NONE);
		TreeColumnLayout layout = new TreeColumnLayout();
		treeContainer.setLayout(layout);

		treeViewer = new TreeViewer(treeContainer, SWT.MULTI
				| SWT.FULL_SELECTION);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);

		keyColumn = new TreeColumn(tree, SWT.NONE);
		keyColumn.setText("Key");
		layout.setColumnData(keyColumn, new ColumnWeightData(2));

		valueColumn = new TreeColumn(tree, SWT.NONE);
		valueColumn.setText("Value");
		layout.setColumnData(valueColumn, new ColumnWeightData(3));

		int index = addPage(treeContainer);
		setPageText(index, "Properties");
		getSite().setSelectionProvider(treeViewer);
	}
	/**创建属性对应的编码页面*/
	void createSourcePage() {
		try {
			textEditor = new TextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, "Source");
		} catch (PartInitException e) {
			FavoritesLog.logError("Error creating nested text editor", e);
		}
	}
	/**更新编辑器标题*/
	void updateTitle() {
		IEditorInput input = getEditorInput();
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(1);
		((IGotoMarker) textEditor.getAdapter(IGotoMarker.class))
				.gotoMarker(marker);
	}
	/**初始化树的内容*/
	void initTreeContent() {
		treeContentProvider = new PropertiesEditorContentProvider();
		treeViewer.setContentProvider(treeContentProvider);
		treeLabelProvider = new PropertiesEditorLabelProvider();
		treeViewer.setLabelProvider(treeLabelProvider);
		treeViewer.setInput(new PropertyFile(""));
		treeViewer.getTree().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				updateTreeFromTextEditor();
			}
		});
	}
	/**根据Source编辑器更新树的内容*/
	void updateTreeFromTextEditor() {
		PropertyFile propertyFile = (PropertyFile) treeViewer.getInput();
		propertyFile.removePropertyFileListener(propertyFileListener);
		propertyFile = new PropertyFile(textEditor.getDocumentProvider()
				.getDocument(textEditor.getEditorInput()).get());
		treeViewer.setInput(propertyFile);
		propertyFile.addPropertyFileListener(propertyFileListener);
	}
	/**根据属性页面更新Source编辑器*/
	void updateTextEditorFromTree() {
		textEditor.getDocumentProvider()
				.getDocument(textEditor.getEditorInput())
				.set(((PropertyFile) treeViewer.getInput()).asText());
	}
	/**初始化树的内联编辑器，使得树可以进行点击后编辑*/
	void initTreeEditors() {
		TreeViewerColumn column1 = new TreeViewerColumn(treeViewer, keyColumn);
		TreeViewerColumn column2 = new TreeViewerColumn(treeViewer, valueColumn);

		column1.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return treeLabelProvider.getColumnText(element, 0);
			}
		});
		column2.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return treeLabelProvider.getColumnText(element, 1);
			}
		});
		column1.setEditingSupport(new EditingSupport(treeViewer) {
			TextCellEditor editor = null;

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) treeViewer.getControl();
					editor = new TextCellEditor(tree);
					editor.setValidator(new ICellEditorValidator() {

						@Override
						public String isValid(Object value) {
							if (((String) value).trim().length() == 0) {
								return "Key must not be empty string";
							}
							return null;
						}
					});
					editor.addListener(new ICellEditorListener() {

						@Override
						public void editorValueChanged(boolean oldValidState,
								boolean newValidState) {
							setErrorMessage(editor.getErrorMessage());
						}

						@Override
						public void cancelEditor() {
							setErrorMessage(null);
						}

						@Override
						public void applyEditorValue() {
							setErrorMessage(null);
						}

						private void setErrorMessage(String errorMessage) {
							getEditorSite().getActionBars()
									.getStatusLineManager()
									.setErrorMessage(errorMessage);
						}
					});
				}
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return treeLabelProvider.getColumnText(element, 0);
			}

			@Override
			protected void setValue(Object element, Object value) {
				if (value == null) {
					return;
				}
				String text = ((String) value).trim();
				if (element instanceof PropertyCategory) {
					((PropertyCategory) element).setName(text);
				}
				if (element instanceof PropertyEntry) {
					((PropertyEntry) element).setKey(text);
				}
			}

		});
		column2.setEditingSupport(new EditingSupport(treeViewer) {
			TextCellEditor editor = null;

			@Override
			protected void setValue(Object element, Object value) {
				String text = ((String) value).trim();
				if (element instanceof PropertyEntry) {
					((PropertyEntry) element).setValue(text);
				}
			}

			@Override
			protected Object getValue(Object element) {
				return treeLabelProvider.getColumnText(element, 1);
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) treeViewer.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof PropertyEntry;
			}
		});
		treeViewer.getColumnViewerEditor().addEditorActivationListener(
				new AltClickCellEditListener());
	}
	/**判断树是否被修改过*/
	public void treeModified() {
		boolean wasDirty = isDirty();
		isPageModified = true;
		if (!wasDirty) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}
	/**覆盖handlePropertyChange方法，使得该方法更适合编辑器页面。这个方法判断了当前编辑器是否被修改过*/
	@Override
	protected void handlePropertyChange(int propertyId) {
		if (propertyId == IEditorPart.PROP_DIRTY) {
			isPageModified = isDirty();
		}
		super.handlePropertyChange(propertyId);
	}

	@Override
	public boolean isDirty() {
		return isPageModified || super.isDirty();
	}
	/**覆盖页面被切换的方法，当页面切换，则根据一个编辑器更新另一个编辑器*/
	@Override
	protected void pageChange(int newPageIndex) {
		switch (newPageIndex) {
		case 0:
			if (isDirty()) {
				updateTreeFromTextEditor();
			}
			setTreeUndoRedo();
			break;
		case 1:
			if (isPageModified) {
				updateTextEditorFromTree();
			}
			setTextEditorUndoRedo();
			break;
		}
		isPageModified = false;
		super.pageChange(newPageIndex);
	}
	/**创建上下文菜单*/
	private void createContextMenu() {
		MenuManager menuMgr=new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				PropertiesEditor.this.fillContextMenu(manager);
			}
		});
		
		Tree tree=treeViewer.getTree();
		Menu menu=menuMgr.createContextMenu(tree);
		tree.setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}
	/**为填充菜单做准备，也可以在这里填充，最好的方式是在插件清单中进行配置，这里首先要增加一个编辑点和提供给其它插件用的菜单插入点*/
	private void fillContextMenu(IMenuManager manager) {
		manager.add(undoAction);
		manager.add(redoAction);
		manager.add(new Separator());
		manager.add(new Separator("edit"));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void initKeyBindingContext() {
		final IContextService service = (IContextService) getSite().getService(
				IContextService.class);
		treeViewer.getControl().addFocusListener(new FocusListener() {
			IContextActivation currentContext = null;

			public void focusGained(FocusEvent e) {
				if (currentContext == null)
					currentContext = service
							.activateContext("com.qualityeclipse.properties.editor.context");
			}

			public void focusLost(FocusEvent e) {
				if (currentContext != null)
					service.deactivateContext(currentContext);
			}
		});
	}
	public IOperationHistory getOperationHistory() {
	      
	      // The workbench provides its own undo/redo manager
	      //return PlatformUI.getWorkbench()
	      //   .getOperationSupport().getOperationHistory();
	      
	      // which, in this case, is the same as the default undo manager
	      return OperationHistoryFactory.getOperationHistory();
	   }
	public IUndoContext getUndoContext() {
	      
	      // For workbench-wide operations, we should return
	      //return PlatformUI.getWorkbench()
	      //   .getOperationSupport().getUndoContext();
	      
	      // but our operations are all local, so return our own content
	      return undoContext;
	   }
	private void initUndoRedo() {
	      undoContext = new ObjectUndoContext(this);
	      undoAction = new UndoActionHandler(getSite(), undoContext);
	      redoAction = new RedoActionHandler(getSite(), undoContext);
	   }
	   
	   private void setTreeUndoRedo() {
	      final IActionBars actionBars = getEditorSite().getActionBars();
	      actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
	      actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
	      actionBars.updateActionBars();
	   }

	   private void setTextEditorUndoRedo() {
	      final IActionBars actionBars = getEditorSite().getActionBars();
	      IAction undoAction2 = textEditor.getAction(ActionFactory.UNDO.getId());
	      actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction2);
	      IAction redoAction2 = textEditor.getAction(ActionFactory.REDO.getId());
	      actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction2);
	      actionBars.updateActionBars();
	      getOperationHistory().dispose(undoContext, true, true, false);
	   }
}
