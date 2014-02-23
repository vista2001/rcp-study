package com.qualityeclipse.favorites.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.qualityeclipse.favorites.FavoritesLog;
import com.qualityeclipse.favorites.actions.DeleteResourcesOperation;

public class DeleteResourcesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		final Collection<IResource> resources = new HashSet<IResource>();
		for (Iterator<?> iterator = structuredSelection.iterator(); iterator
				.hasNext();) {
			Object element = iterator.next();
			if (element instanceof IAdaptable) {
				element = ((IAdaptable) element).getAdapter(IResource.class);
			}
			if (element instanceof IResource) {
				resources.add((IResource) element);
			}
		}
		try {
			// Display progress either using the ProgressMonitorDialog ...
			// Shell shell = HandlerUtil.getActiveShell(event);
			// IRunnableContext context = new ProgressMonitorDialog(shell);

			// ... or using the window's status bar
			// IWorkbenchWindow context =
			// HandlerUtil.getActiveWorkbenchWindow(event);

			// ... or using the workbench progress service
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindow(event);
			IRunnableContext context = window.getWorkbench()
					.getProgressService();

			context.run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					new DeleteResourcesOperation(resources
							.toArray(new IResource[resources.size()]))
							.run(monitor);
				}
			});
		} catch (Exception e) {
			FavoritesLog.logError(e);
		}
		return null;
	}

}
