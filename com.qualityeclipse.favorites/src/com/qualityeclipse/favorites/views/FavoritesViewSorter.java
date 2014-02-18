package com.qualityeclipse.favorites.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;

public class FavoritesViewSorter extends ViewerSorter {
	/** 以下定义的静态常量用于保存排序信息到本地时使用 */
	private static final String TAG_DESCENDING = "descending";
	private static final String TAG_COLUMN_INDEX = "columnIndex";
	private static final String TAG_TYPE = "SortInfo";
	private static final String TAG_TRUE = "true";
	/** 保存的排序信息 */
	private SortInfo[] infos;
	/** 当前的视图 */
	private TableViewer viewer;

	/**视图排序器的构造方法
	 * @param TableViewer viewer 
	 * @param TableColumn [] columns
	 * */
	public FavoritesViewSorter(TableViewer viewer, TableColumn[] columns,
			Comparator<Object>[] comparators) {
		this.viewer = viewer;
		//根据传入的表格列数创建排序信息对象的数组
		infos = new SortInfo[columns.length];
		//为每一列创建排序信息对象
		for (int i = 0; i < columns.length; i++) {
			infos[i] = new SortInfo();
			infos[i].columnIndex = i;
			infos[i].comparator = comparators[i];
			infos[i].descending = false;
			createSelectionListener(columns[i], infos[i]);
			System.out.println(columns[i].getText()+"--"+infos[i].toString());
		}
	}
	
	@Override
	public int compare(Viewer viewer, Object favorite1, Object favorite2) {
		for (int i = 0; i < infos.length; i++) {
			int result = infos[i].comparator.compare(favorite1, favorite2);
			if (result != 0) {
				if (infos[i].descending) {
					return -result;
				}
				return result;
			}
		}
		return 0;
	}
	
	/**给要排序的列添加侦听者，当选中一列时即点击后就进行排序*/
	private void createSelectionListener(final TableColumn column,
			final SortInfo info) {
		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//调用排序方法
				sortUsing(info);
			}
		});
	}
	/**执行排序*/
	protected void sortUsing(SortInfo info) {
		if (info == infos[0]) {
			info.descending = !info.descending;
		} else {
			for (int i = 0; i < infos.length; i++) {
				if (info == infos[i]) {
					System.arraycopy(infos, 0, infos, 1, i);
					infos[0] = info;
					info.descending = false;
					break;
				}
			}
		}
		viewer.refresh();
	}
	/**保存排序状态到IMemento的xml中*/
	public void saveState(IMemento memento) {
		for (int i = 0; i < infos.length; i++) {
			SortInfo info = infos[i];
			IMemento mem = memento.createChild(TAG_TYPE);
			mem.putInteger(TAG_COLUMN_INDEX, info.columnIndex);
			if (info.descending)
				mem.putString(TAG_DESCENDING, TAG_TRUE);
		}
	}
	/**IMemento的xml中获取信息并初始化*/
	public void init(IMemento memento) {
		List<SortInfo> newInfos = new ArrayList<SortInfo>(infos.length);
		IMemento[] mems = memento.getChildren(TAG_TYPE);
		for (int i = 0; i < mems.length; i++) {
			IMemento mem = mems[i];
			Integer value = mem.getInteger(TAG_COLUMN_INDEX);
			if (value == null)
				continue;
			int index = value.intValue();
			if (index < 0 || index >= infos.length)
				continue;
			SortInfo info = infos[index];
			if (newInfos.contains(info))
				continue;
			info.descending = TAG_TRUE.equals(mem.getString(TAG_DESCENDING));
			newInfos.add(info);
		}
		for (int i = 0; i < infos.length; i++)
			if (!newInfos.contains(infos[i]))
				newInfos.add(infos[i]);
		infos = newInfos.toArray(new SortInfo[newInfos.size()]);
	}

	/** 自定义的排序信息类 */
	private class SortInfo {
		/** 列信息 */
		int columnIndex;
		/** 使用的排序比较器 */
		Comparator<Object> comparator;
		/** 升序/降序标志 */
		boolean descending;
	}
}
