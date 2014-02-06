package com.qualityeclipse.favorites.views;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

public class FavoritesViewSorter extends ViewerSorter {
	private class SortInfo {
		int columnIndex;
		Comparator<Object> comparator;
		boolean descending;
	}

	private TableViewer viewer;
	private SortInfo[] infos;

	public FavoritesViewSorter(TableViewer viewer, TableColumn[] columns,
			Comparator<Object>[] comparators) {
		this.viewer = viewer;
		infos = new SortInfo[columns.length];
		for (int i = 0; i < columns.length; i++) {
			infos[i] = new SortInfo();
			infos[i].columnIndex = i;
			infos[i].comparator = comparators[i];
			infos[i].descending = false;
			createSelectionListener(columns[i], infos[i]);
		}
	}

	private void createSelectionListener(final TableColumn column,
			final SortInfo info) {
		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				sortUsing(info);
			}
		});
	}

	protected void sortUsing(SortInfo info) {
		if(info ==infos[0]){
			info.descending =!info.descending;
		}else{
			for(int i=0;i<infos.length;i++){
				if(info == infos[i]){
					System.arraycopy(infos, 0, infos, 1, i);
					infos[0]=info;
					info.descending=false;
					break;
				}
			}
		}
		viewer.refresh();
	}

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
}
