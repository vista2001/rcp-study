package com.qualityeclipse.favorites.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;

import com.qualityeclipse.favorites.dialogs.FavoritesFilterDialog;
import com.qualityeclipse.favorites.views.FavoritesViewLocationFilter;
import com.qualityeclipse.favorites.views.FavoritesViewNameFilter;
import com.qualityeclipse.favorites.views.FavoritesViewTypeFilter;

/**
 * This action prompts the user for a string with wildcards used to filter the
 * favorites view. Any favorite item with a name matching the specified string
 * will be filtered from the Favorites view.
 * <p>
 * This could (should?) be implemented as a subclass of {@link ContributionItem}
 * but instead we subclass Action to illustrate the older approach.
 */
public class FavoritesViewFilterAction extends Action {
	private final Shell shell;
	private final FavoritesViewNameFilter nameFilter;
	private final FavoritesViewLocationFilter locationFilter;
	private final FavoritesViewTypeFilter typeFilter;
	
	public FavoritesViewFilterAction(StructuredViewer viewer, String text) {
		super(text);
		shell = viewer.getControl().getShell();
		nameFilter = new FavoritesViewNameFilter(viewer);
		locationFilter = new FavoritesViewLocationFilter(viewer);
	    typeFilter = new FavoritesViewTypeFilter(viewer);
	}

	public void run() {
		/*
		 * InputDialog dialog = new InputDialog(shell, "Favorites View Filter",
		 * "Enter a name filter pattern" +
		 * " (* = any string, ? = any character)" +
		 * System.getProperty("line.separator") +
		 * "or an empty string for no filtering:", nameFilter.getPattern(),
		 * null); if (dialog.open() == InputDialog.OK)
		 * nameFilter.setPattern(dialog.getValue().trim());
		 */
		FavoritesFilterDialog dialog = new FavoritesFilterDialog(shell,
				nameFilter.getPattern(),locationFilter.getPattern(), typeFilter.getTypes());
		if (dialog.open() != InputDialog.OK)
			return;
		nameFilter.setPattern(dialog.getNamePattern());
		locationFilter.setPattern(dialog.getLocationPattern());
		typeFilter.setTypes(dialog.getSelectedTypes());
	}

	/** 保存过滤器信息 */
	public void saveState(IMemento memento) {
		nameFilter.saveState(memento);
	}

	/** 根据保存的过滤器信息初始化过滤器 */
	public void init(IMemento memento) {
		nameFilter.init(memento);
	}

}
