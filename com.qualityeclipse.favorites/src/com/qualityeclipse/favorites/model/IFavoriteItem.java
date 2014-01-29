package com.qualityeclipse.favorites.model;

import org.eclipse.core.runtime.IAdaptable;

/**
 * ���Ӽ̳���IAdaptable�ӿڣ��ڶ�����FavoriteI�����ͳһ��Ϊ��
 * */
public interface IFavoriteItem extends IAdaptable {
	/**
	 * ��ȡFavorite���������
	 * */
	String getName();

	/**
	 * ����Favorite���������
	 * */
	void setName(String newName);

	/**
	 * ���Favorite�����·����Ϣ
	 * */
	String getLocation();

	/**
	 * �жϴ�������Ƿ���һ��Favorite����
	 * */
	boolean isFavoriteFor(Object obj);

	/**
	 * ���Favorite��������
	 * */
	FavoriteItemType getType();

	/**
	 * ��ȡFavorite�������Ϣ
	 * */
	String getInfo();

	/**
	 * һ����̬IFavoriteItem�������
	 * */
	static IFavoriteItem[] NONE = new IFavoriteItem[] {};
}