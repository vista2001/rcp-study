package com.qualityeclipse.favorites.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class AddToFavoritesActionDelegate implements IObjectActionDelegate , IViewActionDelegate {

	private IWorkbenchPart targetPart;

	@Override
	public void run(IAction action) {
		MessageDialog.openInformation(targetPart.getSite().getShell(),
				"Add to Favorites", "Triggered the " + getClass().getName()
						+ " action");
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	@Override
	public void init(IViewPart view) {
		this.targetPart =  view;
	}

}
