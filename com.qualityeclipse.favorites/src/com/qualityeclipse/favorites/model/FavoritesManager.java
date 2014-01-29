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
	 * ʹ��private�ؼ����������캯�������ṩһ�������൱ǰʵ���ķ�����ʹ���˵���ģʽ�� ͨ������ϣ����ȫ�ַ�Χ�ڻ��ͳһ�Ķ���������ʵ��
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

	// ���·����ǲ���Favorite����ķ���
	/**
	 * װ��Favorite����Ŀǰ��Ӳ����
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
	 * ���һ��Object���󵽵�ǰ��IFavoriteItem���󼯺���
	 * */
	public void addFavorites(Object[] objects) {
		if (objects == null)
			return;
		// �ж���ǰ�����Ƿ���ڣ���������װ�ض���
		if (favorites == null)
			loadFavorites();
		// ��Ҫ��ӵ���ǰIFavoriteItem���󼯺��еļ��ϣ���ŵ��Ǵ����Object[]�����з��ϲ��������Ķ���
		Collection<IFavoriteItem> items = new HashSet<IFavoriteItem>(
				objects.length);
		// ��������Ķ��󼯺�
		for (int i = 0; i < objects.length; i++) {
			// ����ÿһ������Ķ����Ƿ��ڵ�ǰIFavoriteItem���󼯺��д���
			IFavoriteItem item = existingFavoriteFor(objects[i]);
			// ��Ӵ�����󵽵�ǰIFavoriteItem���󼯺��У�ע�������������ģ�Ͳ������
			if (item == null) {
				item = newFavoriteFor(objects[i]);
				if (favorites.add(item))
					items.add(item);
			}
		}
		// �ѷ��������Ķ��󼯺���ӵ���ǰIFavoriteItem���󼯺��к󣬴��������¼���֪ͨ������
		if (items.size() > 0) {
			IFavoriteItem[] added = items.toArray(new IFavoriteItem[items
					.size()]);
			fireFavoritesChanged(added, IFavoriteItem.NONE);
		}
	}

	/**
	 * �ӵ�ǰIFavoriteItem���󼯺���ɾ�������Object����
	 * */
	public void removeFavorites(Object[] objects) {
		if (objects == null) {
			return;
		}
		// �ж���ǰ�����Ƿ���ڣ���������װ�ض���
		if (favorites == null) {
			loadFavorites();
		}
		// ��Ҫ�ӵ�ǰIFavoriteItem���󼯺���ɾ���ļ��ϣ���ŵ��Ǵ����Object[]�����з���ɾ�������Ķ���
		Collection<IFavoriteItem> items = new HashSet<IFavoriteItem>(
				objects.length);
		// ����ÿһ������Ķ���
		for (int i = 0; i < objects.length; i++) {
			// ����ÿһ������Ķ����Ƿ��ڵ�ǰIFavoriteItem���󼯺��д���
			IFavoriteItem item = existingFavoriteFor(objects[i]);
			// �ӵ�ǰIFavoriteItem���󼯺���ɾ���������
			if (item != null && favorites.remove(item)) {
				items.add(item);
			}
		}
		// �ѷ��������Ķ��󼯺ϴӵ�ǰIFavoriteItem���󼯺���ɾ���󣬴��������¼���֪ͨ������
		if (items.size() > 0) {
			IFavoriteItem[] removed = items.toArray(new IFavoriteItem[items
					.size()]);
			fireFavoritesChanged(IFavoriteItem.NONE, removed);
		}
	}

	/**
	 * ����һ���µ�IFavoriteItem����
	 * */
	public IFavoriteItem newFavoriteFor(String typeId, String info) {
		FavoriteItemType[] types = FavoriteItemType.getTypes();
		for (int i = 0; i < types.length; i++)
			if (types[i].getId().equals(typeId))
				return types[i].loadFavorite(info);
		return null;
	}

	/**
	 * ����һ���µ�IFavoriteItem����
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
	 * �ж�һ������Ķ����Ƿ������е�IFavoriteItem���󼯺��д���
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
	 * �жϴ���ĵ������еĶ����Ƿ��ڵ�ǰ��IFavoriteItem���󼯺��У�������һ���Ѿ����ڵĶ��󼯺�
	 * */
	public IFavoriteItem[] existingFavoritesFor(Iterator<?> iter) {
		List<IFavoriteItem> result = new ArrayList<IFavoriteItem>(10);
		while (iter.hasNext()) {
			IFavoriteItem item = existingFavoriteFor(iter.next());
			if (item != null) {
				result.add(item);
			}
		}
		// �����ǿ��ת��Ӧ�ÿ��Բ��ã�
		return (IFavoriteItem[]) result
				.toArray(new IFavoriteItem[result.size()]);
	}

	// ���·������������Ĵ���
	/**
	 * ��ΪFavoriteManager��������������ȫ�ֵ�Favorite����ģ���˵�Favorite���󼯺Ϸ����仯ʱ��
	 * Ӧ����FavoriteManager֪ͨ��Favorite����Ϊ�������ݵ�������
	 * ʵ���������Favorite���󼯺Ͼ��������ߵ�����ģ�ͣ�����MVC�����ۣ���M�ı�ʱӦ��ͨ��C֪ͨV
	 * */
	/**
	 * ��������
	 * */
	public void addFavoritesManagerListener(FavoritesManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * �Ƴ�����
	 * */
	public void removeFavoritesListener(FavoritesManagerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * ��Favorite���󼯺ϵ����ݷ����仯ʱ������һ��FavoritesManagerEvent�¼�����֪ͨ������
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
