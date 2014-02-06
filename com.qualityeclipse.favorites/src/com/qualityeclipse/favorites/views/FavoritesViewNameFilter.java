package com.qualityeclipse.favorites.views;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.qualityeclipse.favorites.model.IFavoriteItem;
import com.qualityeclipse.favorites.util.StringMatcher;

public class FavoritesViewNameFilter extends ViewerFilter {

	private final StructuredViewer viewer;
	private String pattern = "";
	private StringMatcher matcher;

	public FavoritesViewNameFilter(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String newPattern) {
		boolean filtering = matcher != null;
		if (newPattern != null && newPattern.trim().length() > 0) {
			pattern = newPattern;
			matcher = new StringMatcher(pattern, true, false);
			if (!filtering) {
				viewer.addFilter(this);
			} else {
				viewer.refresh();
			}
		} else {
			pattern = "";
			matcher = null;
			if (filtering) {
				viewer.removeFilter(this);
			}
		}
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return matcher.match(((IFavoriteItem) element).getName());
	}

}
