package com.qualityeclipse.favorites.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class DeleteResourcesOperation extends WorkspaceModifyOperation {

	private final IResource[] resources;

	public DeleteResourcesOperation(IResource[] resources) {
		this.resources = resources;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("Deleting resources...", resources.length);
				for (int i = 0; i < resources.length; i++) {
					if (monitor.isCanceled())
						break;
					resources[i].delete(true,
							new SubProgressMonitor(monitor, 1));
				}

				monitor.done();
			}
		}, monitor);
	}
}
