package com.qualityeclipse.favorites.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.qualityeclipse.favorites.model.FavoritesManager;

public class RemoveFavoritesHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
	      if (selection instanceof IStructuredSelection)
	         FavoritesManager.getManager().removeFavorites(
	               ((IStructuredSelection) selection).toArray());
		return null;
	}

}
