package com.qualityeclipse.favorites.model;

import org.eclipse.swt.graphics.Image;

public abstract class FavoriteItemType implements Comparable<FavoriteItemType> {
	private final String id;
	private final String printName;
	private final int ordinal;

	private FavoriteItemType(String id, String name, int position) {
		this.id = id;
		this.ordinal = position;
		this.printName = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return printName;
	}

	public abstract Image getImage();

	public abstract IFavoriteItem newFavorite(Object obj);

	public abstract IFavoriteItem loadFavorite(String info);

	public int compareTo(FavoriteItemType other) {
		return this.ordinal - other.ordinal;
	}

	/**
	 * Constants representing types of favorites objects.
	 * */
	public static final FavoriteItemType UNKNOWN = new FavoriteItemType(
			"Unknown", "Unknown", 0) {
		public Image getImage() {
			return null;
		}

		public IFavoriteItem newFavorite(Object obj) {
			return null;
		}

		public IFavoriteItem loadFavorite(String info) {
			return null;
		}
	};
	
	public static final FavoriteItemType WORKBENCH_FILE 
    = new FavoriteItemType("WBFile", "Workbench File", 1){

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			return null;
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return null;
		}
		
	};
	
}
