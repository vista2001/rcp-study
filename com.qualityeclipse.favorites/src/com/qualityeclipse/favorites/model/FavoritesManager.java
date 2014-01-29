package com.qualityeclipse.favorites.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class FavoritesManager {
	private static FavoritesManager manager;
	private Collection<IFavoriteItem> favorites;
	private List<FavoritesManagerListener> listeners = new ArrayList<FavoritesManagerListener>();

	/**
	 * 使用private关键字声明构造函数，并提供一个返回类当前实例的方法是使用了单例模式， 通常用于希望在全局范围内获得统一的对象的情况下实用
	 * */
	private FavoritesManager() {

	}

	public static FavoritesManager getManager() {
		if (manager == null) {
			manager = new FavoritesManager();
		}
		return manager;
	}

	public IFavoriteItem[] getFavorites() {
		if (favorites == null)
			loadFavorites();
		return favorites.toArray(new IFavoriteItem[favorites.size()]);
	}

	// 以下方法是操作Favorite对象的方法
	/**
	 * 装载Favorite对象，目前是硬编码
	 * */
	private void loadFavorites() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		// create a HashSet with initial capacity of given number
		favorites = new HashSet(projects.length);
		for (int i = 0; i < projects.length; i++) {
			favorites.add(new FavoriteResource(
					FavoriteItemType.WORKBENCH_PROJECT, projects[i]));
		}
	}

	/**
	 * 添加一组Object对象到当前的IFavoriteItem对象集合中
	 * */
	public void addFavorites(Object[] objects) {
		if (objects == null)
			return;
		// 判定当前集合是否存在，不存在则装载对象
		if (favorites == null)
			loadFavorites();
		// 需要添加到当前IFavoriteItem对象集合中的集合，存放的是传入的Object[]数组中符合插入条件的对象
		Collection<IFavoriteItem> items = new HashSet<IFavoriteItem>(
				objects.length);
		// 遍历传入的对象集合
		for (int i = 0; i < objects.length; i++) {
			// 检验每一个传入的对象是否在当前IFavoriteItem对象集合中存在
			IFavoriteItem item = existingFavoriteFor(objects[i]);
			// 添加传入对象到当前IFavoriteItem对象集合中，注意这里操作的是模型层的内容
			if (item == null) {
				item = newFavoriteFor(objects[i]);
				if (favorites.add(item))
					items.add(item);
			}
		}
		// 把符合条件的对象集合添加到当前IFavoriteItem对象集合中后，触发侦听事件，通知侦听者
		if (items.size() > 0) {
			IFavoriteItem[] added = items.toArray(new IFavoriteItem[items
					.size()]);
			fireFavoritesChanged(added, IFavoriteItem.NONE);
		}
	}

	/**
	 * 从当前IFavoriteItem对象集合中删除传入的Object对象
	 * */
	public void removeFavorites(Object[] objects) {
		if (objects == null) {
			return;
		}
		// 判定当前集合是否存在，不存在则装载对象
		if (favorites == null) {
			loadFavorites();
		}
		// 需要从当前IFavoriteItem对象集合中删除的集合，存放的是传入的Object[]数组中符合删除条件的对象
		Collection<IFavoriteItem> items = new HashSet<IFavoriteItem>(
				objects.length);
		// 遍历每一个传入的对象
		for (int i = 0; i < objects.length; i++) {
			// 检验每一个传入的对象是否在当前IFavoriteItem对象集合中存在
			IFavoriteItem item = existingFavoriteFor(objects[i]);
			// 从当前IFavoriteItem对象集合中删除传入对象
			if (item != null && favorites.remove(item)) {
				items.add(item);
			}
		}
		// 把符合条件的对象集合从当前IFavoriteItem对象集合中删除后，触发侦听事件，通知侦听者
		if (items.size() > 0) {
			IFavoriteItem[] removed = items.toArray(new IFavoriteItem[items
					.size()]);
			fireFavoritesChanged(IFavoriteItem.NONE, removed);
		}
	}

	/**
	 * 创建一个新的IFavoriteItem对象
	 * */
	public IFavoriteItem newFavoriteFor(String typeId, String info) {
		FavoriteItemType[] types = FavoriteItemType.getTypes();
		for (int i = 0; i < types.length; i++)
			if (types[i].getId().equals(typeId))
				return types[i].loadFavorite(info);
		return null;
	}

	/**
	 * 创建一个新的IFavoriteItem对象
	 * */
	public IFavoriteItem newFavoriteFor(Object obj) {
		FavoriteItemType[] types = FavoriteItemType.getTypes();
		for (int i = 0; i < types.length; i++) {
			IFavoriteItem item = types[i].newFavorite(obj);
			if (item != null)
				return item;
		}
		return null;
	}

	/**
	 * 判定一个传入的对象是否在现有的IFavoriteItem对象集合中存在
	 * */
	private IFavoriteItem existingFavoriteFor(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof IFavoriteItem)
			return (IFavoriteItem) obj;
		Iterator<IFavoriteItem> iter = favorites.iterator();
		while (iter.hasNext()) {
			IFavoriteItem item = iter.next();
			if (item.isFavoriteFor(obj))
				return item;
		}
		return null;
	}

	/**
	 * 判断传入的迭代器中的对象是否在当前的IFavoriteItem对象集合中，并返回一个已经存在的对象集合
	 * */
	public IFavoriteItem[] existingFavoritesFor(Iterator<?> iter) {
		List<IFavoriteItem> result = new ArrayList<IFavoriteItem>(10);
		while (iter.hasNext()) {
			IFavoriteItem item = existingFavoriteFor(iter.next());
			if (item != null) {
				result.add(item);
			}
		}
		// 这里的强制转换应该可以不用？
		return (IFavoriteItem[]) result
				.toArray(new IFavoriteItem[result.size()]);
	}

	// 以下方法用于侦听的处理
	/**
	 * 因为FavoriteManager对象是用来管理全局的Favorite对象的，因此当Favorite对象集合发生变化时，
	 * 应当由FavoriteManager通知以Favorite对象为输入内容的侦听者
	 * 实际上这里的Favorite对象集合就是侦听者的数据模型，根据MVC的理论，当M改变时应该通过C通知V
	 * */
	/**
	 * 增加侦听
	 * */
	public void addFavoritesManagerListener(FavoritesManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * 移除侦听
	 * */
	public void removeFavoritesListener(FavoritesManagerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 当Favorite对象集合的内容发生变化时，产生一个FavoritesManagerEvent事件，并通知侦听者
	 * */
	private void fireFavoritesChanged(IFavoriteItem[] itemsAdded,
			IFavoriteItem[] itemsRemoved) {
		FavoritesManagerEvent event = new FavoritesManagerEvent(this,
				itemsAdded, itemsRemoved);
		for (Iterator<FavoritesManagerListener> iter = listeners.iterator(); iter
				.hasNext();)
			iter.next().favoritesChanged(event);
	}

}
