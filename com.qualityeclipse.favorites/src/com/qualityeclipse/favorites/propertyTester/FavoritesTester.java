package com.qualityeclipse.favorites.propertyTester;

import com.qualityeclipse.favorites.model.FavoritesManager;
import com.qualityeclipse.favorites.model.IFavoriteItem;

public class FavoritesTester extends
		org.eclipse.core.expressions.PropertyTester {

	public FavoritesTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		boolean found = false;
		IFavoriteItem[] favorites = FavoritesManager.getManager()
				.getFavorites();
		for (int i = 0; i < favorites.length; i++) {
			IFavoriteItem item = favorites[i];
			found = item.isFavoriteFor(receiver);
			if (found)
				break;
		}
		if ("isFavorite".equals(property)) {
			return found;
		}
		if ("notFavorite".equals(property)) {
			return !found;
		}
		return false;
	}

}
