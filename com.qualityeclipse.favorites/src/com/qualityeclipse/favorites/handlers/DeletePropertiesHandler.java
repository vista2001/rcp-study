package com.qualityeclipse.favorites.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.qualityeclipse.favorites.editors.PropertiesEditor;
import com.qualityeclipse.favorites.editors.PropertyElement;

public class DeletePropertiesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		final IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		if (!(editorPart instanceof PropertiesEditor)) {
			return null;
		}
		return execute((PropertiesEditor) editorPart,
				(IStructuredSelection) selection);
	}

	private Object execute(final PropertiesEditor editor,
			IStructuredSelection selection) {
		Iterator<?> iterator = selection.iterator();
		int size = selection.size();
		PropertyElement[] elements = new PropertyElement[size];
		for (int i = 0; i < size; i++) {
			elements[i] = (PropertyElement) ((Object) iterator.next());
		}
		
		DeletePropertiesOperation op = new DeletePropertiesOperation(elements);
		op.addContext(editor.getUndoContext());

		IProgressMonitor monitor = 
				editor
				.getEditorSite()
				.getActionBars()
				.getStatusLineManager()
				.getProgressMonitor();
		
		IAdaptable info = new IAdaptable() {

			@Override
			public Object getAdapter(Class adapter) {
				if (Shell.class.equals(adapter)) {
					return editor.getSite().getShell();
				}
				return null;
			}
		};
		
		try {
			editor.getOperationHistory().execute(op, monitor, info);
		} catch (ExecutionException e) {
			MessageDialog.openError(editor.getSite().getShell(),
					"Remove Properties Error",
					"Exception while removing properties:" + e.getMessage());
		}
		return null;
	}

}
