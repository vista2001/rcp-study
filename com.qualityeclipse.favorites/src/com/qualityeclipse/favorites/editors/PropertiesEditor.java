package com.qualityeclipse.favorites.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.qualityeclipse.favorites.FavoritesLog;

public class PropertiesEditor extends MultiPageEditorPart {
	
	private TreeViewer treeViewer;
	private TextEditor textEditor;
	
	private TreeColumn keyColumn;
	private TreeColumn valueColumn;
	
	private PropertiesEditorContentProvider treeContentProvider;
	private PropertiesEditorLabelProvider treeLabelProvider;
	
	@Override
	protected void createPages() {
		createPropertiesPage();
		createSourcePage();
		updateTitle();
		initTreeContent();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		textEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
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
		switch(getActivePage()){
		case 0:
			treeViewer.getTree().setFocus();
			break;
		case 1:
			textEditor.setFocus();
			break;
		}
	}

	void createPropertiesPage(){
		Composite treeContainer = new Composite(getContainer(),SWT.NONE);
		TreeColumnLayout layout=new TreeColumnLayout();
		treeContainer.setLayout(layout);
		
		treeViewer=new TreeViewer(treeContainer,SWT.MULTI|SWT.FULL_SELECTION);
		Tree tree=treeViewer.getTree();
		tree.setHeaderVisible(true);
		
		keyColumn=new TreeColumn(tree, SWT.NONE);
		keyColumn.setText("Key");
		layout.setColumnData(keyColumn, new ColumnWeightData(2));
		
		valueColumn=new TreeColumn(tree, SWT.NONE);
		valueColumn.setText("Value");
		layout.setColumnData(valueColumn, new ColumnWeightData(3));
		
		int index=addPage(treeContainer);
		setPageText(index, "Properties");
	}
	void createSourcePage(){
		try{
			textEditor =new TextEditor();
			int index=addPage(textEditor,getEditorInput());
			setPageText(index, "Source");
		}catch(PartInitException e){
			FavoritesLog.logError("Error creating nested text editor", e);
		}
	}
	void updateTitle(){
		IEditorInput input=getEditorInput();
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
	}
	public void gotoMarker(IMarker marker){
		setActivePage(1);
		((IGotoMarker)textEditor.getAdapter(IGotoMarker.class)).gotoMarker(marker);
	}
	void initTreeContent(){
		treeContentProvider=new PropertiesEditorContentProvider();
		treeViewer.setContentProvider(treeContentProvider);
		treeLabelProvider=new PropertiesEditorLabelProvider();
		treeViewer.setLabelProvider(treeLabelProvider);
		treeViewer.setInput(new PropertyFile(""));
		treeViewer.getTree().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				updateTreeFromTextEditor();
			}
		});
	}
	
	void updateTreeFromTextEditor(){
		PropertyFile propertyFile=new PropertyFile(textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get());
		treeViewer.setInput(propertyFile);
	}
}
