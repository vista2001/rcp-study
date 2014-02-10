package com.qualityeclipse.favorites.views;

import java.util.Comparator;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.qualityeclipse.favorites.actions.FavoritesViewFilterAction;
import com.qualityeclipse.favorites.contributions.RemoveFavoirtesContributionItem;
import com.qualityeclipse.favorites.handlers.RemoveFavoritesHandler;
import com.qualityeclipse.favorites.model.FavoritesManager;
import com.qualityeclipse.favorites.model.IFavoriteItem;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class FavoritesView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.qualityeclipse.favorites.views.FavoritesView";

	private TableViewer viewer;
	private TableColumn typeColumn;
	private TableColumn nameColumn;
	private TableColumn locationColumn;
	private FavoritesViewSorter sorter;
	private IHandler removeHandler;
	private RemoveFavoirtesContributionItem removeContributionItem;
	private FavoritesViewFilterAction filterAction;

	/**
	 * The constructor.
	 */
	public FavoritesView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		createTableViewer(parent);
		createTableSorter();
		createContributions();
		createContextMenu();
		createToolbarButtons();
		createViewPulldownMenu();
		hookKeyboard();
		hookGlobalHandlers();
	}

	private void createTableViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		final Table table = viewer.getTable();
		typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("");
		typeColumn.setWidth(18);

		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("name");
		nameColumn.setWidth(200);

		locationColumn = new TableColumn(table, SWT.LEFT);
		locationColumn.setText("Location");
		locationColumn.setWidth(450);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new FavoritesViewContentProvider());
		viewer.setLabelProvider(new FavoritesViewLabelProvider());
		viewer.setInput(FavoritesManager.getManager());
		// 注册当前视图为选择选择提供者
		getSite().setSelectionProvider(viewer);
	}

	@SuppressWarnings("unchecked")
	private void createTableSorter() {
		Comparator<IFavoriteItem> nameComparator = new Comparator<IFavoriteItem>() {

			@Override
			public int compare(IFavoriteItem o1, IFavoriteItem o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
		Comparator<IFavoriteItem> locationComparator = new Comparator<IFavoriteItem>() {

			@Override
			public int compare(IFavoriteItem o1, IFavoriteItem o2) {
				return o1.getLocation().compareTo(o2.getLocation());
			}
		};
		Comparator<IFavoriteItem> typeComparator = new Comparator<IFavoriteItem>() {

			@Override
			public int compare(IFavoriteItem o1, IFavoriteItem o2) {
				return o1.getType().compareTo(o2.getType());
			}
		};
		sorter = new FavoritesViewSorter(viewer, new TableColumn[] {
				nameColumn, locationColumn, typeColumn }, new Comparator[] {
				nameComparator, locationComparator, typeComparator });
		viewer.setSorter(sorter);
	}

	private void createContributions() {
		removeHandler = new RemoveFavoritesHandler();
		removeContributionItem = new RemoveFavoirtesContributionItem(
				getViewSite(), removeHandler);
	}

	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				FavoritesView.this.fillContextMenu(m);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(new Separator("edit"));
		menuMgr.add(removeContributionItem);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void createToolbarButtons() {
		IToolBarManager toolBarMgr = getViewSite().getActionBars()
				.getToolBarManager();
		toolBarMgr.add(new GroupMarker("edit"));
		toolBarMgr.add(removeContributionItem);
	}

	private void createViewPulldownMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		filterAction = new FavoritesViewFilterAction(viewer, "Filter...");
		menu.add(filterAction);
	}

	private void hookKeyboard() {
		viewer.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e);
			}

		});
	}

	protected void handleKeyReleased(KeyEvent event) {
		if (event.character == SWT.DEL && event.stateMask == 0)
			removeContributionItem.run();
	}

	private void hookGlobalHandlers() {
		final IHandlerService handlerService = (IHandlerService) getViewSite()
				.getService(IHandlerService.class);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			private IHandlerActivation removeActivation;

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					if (removeActivation != null) {
						handlerService.deactivateHandler(removeActivation);
						removeActivation = null;
					}
				} else {
					if (removeActivation == null) {
						removeActivation = handlerService.activateHandler(
								IWorkbenchActionDefinitionIds.DELETE,
								removeHandler);
					}
				}
			}
		});
	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		viewer.addSelectionChangedListener(listener);
	}
}