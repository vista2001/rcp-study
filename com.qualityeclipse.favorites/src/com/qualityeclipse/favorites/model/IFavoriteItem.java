package com.qualityeclipse.favorites.model;

import org.eclipse.core.runtime.IAdaptable;

/**
 * 本接继承自IAdaptable接口，口定义了FavoriteI对象的统一行为。
 * */
public interface IFavoriteItem extends IAdaptable {
	/**
	 * 获取Favorite对象的名称
	 * */
	String getName();

	/**
	 * 设置Favorite对象的名称
	 * */
	void setName(String newName);

	/**
	 * 获得Favorite对象的路径信息
	 * */
	String getLocation();

	/**
	 * 判断传入对象是否是一个Favorite对象
	 * */
	boolean isFavoriteFor(Object obj);

	/**
	 * 获得Favorite对象类型
	 * */
	FavoriteItemType getType();

	/**
	 * 获取Favorite对象的信息
	 * */
	String getInfo();

	/**
	 * 一个静态IFavoriteItem数组对象
	 * */
	static IFavoriteItem[] NONE = new IFavoriteItem[] {};
}