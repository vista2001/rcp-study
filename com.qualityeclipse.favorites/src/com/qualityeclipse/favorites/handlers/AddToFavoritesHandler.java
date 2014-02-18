package com.qualityeclipse.favorites.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.qualityeclipse.favorites.model.FavoritesManager;

public class AddToFavoritesHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		//MessageDialog.openConfirm(null, "Add", "The \"Add to Favorites\" handler was called");
		ISelection selection =HandlerUtil.getCurrentSelection(event);
		if(selection instanceof IStructuredSelection)
			FavoritesManager.getManager().addFavorites(((IStructuredSelection)selection).toArray());
		return null;
	}

}
