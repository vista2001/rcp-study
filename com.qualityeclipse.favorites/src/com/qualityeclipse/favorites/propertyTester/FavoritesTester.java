package com.qualityeclipse.favorites.propertyTester;

public class FavoritesTester extends org.eclipse.core.expressions.PropertyTester {

	public FavoritesTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if("isFavorite".equals(property)){
			return false;
		}
		if("notFavorite".equals(property)){
			return true;
		}
		return false;
	}

}
