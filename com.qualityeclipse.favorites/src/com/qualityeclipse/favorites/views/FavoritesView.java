package com.qualityeclipse.favorites.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.qualityeclipse.favorites.FavoritesActivator;
import com.qualityeclipse.favorites.actions.FavoritesViewFilterAction;
import com.qualityeclipse.favorites.contributions.RemoveFavoirtesContributionItem;
import com.qualityeclipse.favorites.handlers.RemoveFavoritesHandler;
import com.qualityeclipse.favorites.handlers.RenameFavoritesHandler;
import com.qualityeclipse.favorites.model.FavoritesManager;
import com.qualityeclipse.favorites.model.IFavoriteItem;
import com.qualityeclipse.favorites.preferences.PreferenceConstants;
import com.qualityeclipse.favorites.util.EditorUtil;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class FavoritesView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.qualityeclipse.favorites.views.FavoritesView";

	private static final int NAME_COLUMN_INITIAL_WIDTH = 200;
	private static final int LOCATION_COLUMN_INITIAL_WIDTH = 450;
	
	private TableViewer viewer;
	private TableColumn typeColumn;
	private TableColumn nameColumn;
	private TableColumn locationColumn;
	private FavoritesViewSorter sorter;
	private IHandler removeHandler;
	private RemoveFavoirtesContributionItem removeContributionItem;
	private FavoritesViewFilterAction filterAction;
	private ISelectionListener pageSelectionListener;
	private IMemento memento;
	
	private final IPropertyChangeListener propertyChangeListener =
	         new IPropertyChangeListener() {
	            public void propertyChange(PropertyChangeEvent event) {
	               if (event.getProperty().equals(
	                     PreferenceConstants.FAVORITES_VIEW_NAME_COLUMN_VISIBLE)
	                     || event.getProperty().equals(
	                           PreferenceConstants.FAVORITES_VIEW_LOCATION_COLUMN_VISIBLE))
	                  updateColumnWidths();
	            }
	         };
	

	/**
	 * The constructor.
	 */
	public FavoritesView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		createTableViewer(parent);
		updateColumnWidths();
		createTableSorter();
		createContributions();
		createContextMenu();
		createToolbarButtons();
		createViewPulldownMenu();
		hookKeyboard();
		hookGlobalHandlers();
		hookDragAndDrop();
		createInlineEditor();
		hookPageSelection();
		hookMouse();
		FavoritesActivator.getDefault().getPreferenceStore().addPropertyChangeListener(
	            propertyChangeListener);
	}

	private void createTableViewer(Composite parent) {
		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		final Table table = viewer.getTable();
		typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("");
		//typeColumn.setWidth(18);
		layout.setColumnData(typeColumn, new ColumnPixelData(18));

		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("name");
		//nameColumn.setWidth(200);
		layout.setColumnData(nameColumn, new ColumnWeightData(4));

		locationColumn = new TableColumn(table, SWT.LEFT);
		locationColumn.setText("Location");
		//locationColumn.setWidth(450);
		layout.setColumnData(locationColumn, new ColumnWeightData(9));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new FavoritesViewContentProvider());
		viewer.setLabelProvider(new FavoritesViewLabelProvider());
		viewer.setInput(FavoritesManager.getManager());
		// 注册当前视图为选择选择提供者
		getSite().setSelectionProvider(viewer);
	}
	
	private void updateColumnWidths() {
	      IPreferenceStore prefs = FavoritesActivator.getDefault().getPreferenceStore();

	      boolean showNameColumn =
	            prefs.getBoolean(PreferenceConstants.FAVORITES_VIEW_NAME_COLUMN_VISIBLE);
	      nameColumn.setWidth(showNameColumn ? NAME_COLUMN_INITIAL_WIDTH : 0);

	      boolean showLocationColumn =
	            prefs.getBoolean(PreferenceConstants.FAVORITES_VIEW_LOCATION_COLUMN_VISIBLE);
	      locationColumn.setWidth(showLocationColumn ? LOCATION_COLUMN_INITIAL_WIDTH : 0);
	   }
	
	@SuppressWarnings("unchecked")
	private void createTableSorter() {
		Comparator<IFavoriteItem> nameComparator = new Comparator<IFavoriteItem>() {

			@Override
			public int compare(IFavoriteItem o1, IFavoriteItem o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
		Comparator<IFavoriteItem> locationComparator = new Comparator<IFavoriteItem>() {

			@Override
			public int compare(IFavoriteItem o1, IFavoriteItem o2) {
				return o1.getLocation().compareTo(o2.getLocation());
			}
		};
		Comparator<IFavoriteItem> typeComparator = new Comparator<IFavoriteItem>() {

			@Override
			public int compare(IFavoriteItem o1, IFavoriteItem o2) {
				return o1.getType().compareTo(o2.getType());
			}
		};
		sorter = new FavoritesViewSorter(viewer, new TableColumn[] {
				nameColumn, locationColumn, typeColumn }, new Comparator[] {
				nameComparator, locationComparator, typeComparator });
		
		if (memento != null){
			sorter.init(memento);
		}
		viewer.setSorter(sorter);
	}

	private void createContributions() {
		removeHandler = new RemoveFavoritesHandler();
		removeContributionItem = new RemoveFavoirtesContributionItem(
				getViewSite(), removeHandler);
	}

	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				FavoritesView.this.fillContextMenu(m);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(new Separator("edit"));
		menuMgr.add(removeContributionItem);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void createToolbarButtons() {
		IToolBarManager toolBarMgr = getViewSite().getActionBars()
				.getToolBarManager();
		toolBarMgr.add(new GroupMarker("edit"));
		toolBarMgr.add(removeContributionItem);
	}

	private void createViewPulldownMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		filterAction = new FavoritesViewFilterAction(viewer, "Filter...");
		
		if (memento != null){
			filterAction.init(memento);
		}
		menu.add(filterAction);
	}

	private void hookKeyboard() {
		viewer.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e);
			}

		});
	}

	protected void handleKeyReleased(KeyEvent event) {
		if (event.character == SWT.DEL && event.stateMask == 0) {
			removeContributionItem.run();
		}
		if (event.keyCode == SWT.F2 && event.stateMask == 0) {
			new RenameFavoritesHandler().editElement(this);
		}
	}

	private void hookGlobalHandlers() {
		final IHandlerService handlerService = (IHandlerService) getViewSite()
				.getService(IHandlerService.class);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			private IHandlerActivation removeActivation;

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					if (removeActivation != null) {
						handlerService.deactivateHandler(removeActivation);
						removeActivation = null;
					}
				} else {
					if (removeActivation == null) {
						removeActivation = handlerService.activateHandler(
								IWorkbenchCommandConstants.EDIT_DELETE,// IWorkbenchActionDefinitionIds.DELETE,
								removeHandler);
					}
				}
			}
		});
	}

	private void hookDragAndDrop() {
		new FavoritesDragSource(viewer);
		new FavoritesDropTarget(viewer);
	}

	private void createInlineEditor() {
		TableViewerColumn column = new TableViewerColumn(viewer, nameColumn);
		column.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((IFavoriteItem) element).getName();
			}
		});
		column.setEditingSupport(new EditingSupport(viewer) {
			TextCellEditor editor = null;

			@Override
			protected void setValue(Object element, Object value) {
				((IFavoriteItem) element).setName((String) value);
				viewer.refresh(element);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IFavoriteItem) element).getName();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite table = (Composite) viewer.getControl();
					editor = new TextCellEditor(table);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		viewer.getColumnViewerEditor().addEditorActivationListener(
				new AltClickCellEditListener());
				
	}

	private void hookPageSelection() {
		pageSelectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				pageSelectionChanged(part, selection);
			}
		};
		getSite().getPage().addPostSelectionListener(pageSelectionListener);
	}

	private void pageSelectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		IStructuredSelection sel = (IStructuredSelection) selection;
		IFavoriteItem[] items = FavoritesManager.getManager()
				.existingFavoritesFor(sel.iterator());
		if (items.length > 0) {
			viewer.setSelection(new StructuredSelection(items), true);
		}
	}

	private void hookMouse() {
		viewer.getTable().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				EditorUtil.openEditor(getSite().getPage(),
						viewer.getSelection());
			}
		});
	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		viewer.addSelectionChangedListener(listener);
	}

	public TableViewer getFavoritesView() {
		return this.viewer;
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		sorter.saveState(memento);
		filterAction.saveState(memento);
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
	}

	@Override
	public void dispose() {
		if (pageSelectionListener != null)
	         getSite().getPage().removePostSelectionListener(pageSelectionListener);
	    FavoritesActivator.getDefault().getPreferenceStore().removePropertyChangeListener(
	            propertyChangeListener);
	    super.dispose();
	}
}