package com.qualityeclipse.favorites.contributions;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.qualityeclipse.favorites.FavoritesLog;

public class RemoveFavoirtesContributionItem extends ContributionItem {
	private final IViewSite viewSite;
	private final IHandler handler;
	boolean enabled = false;
	private MenuItem menuItem;
	private ToolItem toolItem;

	public RemoveFavoirtesContributionItem(IViewSite viewSite, IHandler handler) {
		// this.view = view;
		this.handler = handler;
		this.viewSite = viewSite;
		viewSite.getSelectionProvider().addSelectionChangedListener(
				new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						enabled = !event.getSelection().isEmpty();
						updateEnablement();
					}
				});
	}

	public void fill(Menu menu, int index) {
		menuItem = new MenuItem(menu, SWT.NONE, index);
		menuItem.setText("Remove");
		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		updateEnablement();
	}

	public void fill(ToolBar parent, int index) {
		toolItem = new ToolItem(parent, SWT.NONE, index);
		toolItem.setToolTipText("Remove the selected favorite items");
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		updateEnablement();
	}

	private void updateEnablement() {
		Image image = PlatformUI
				.getWorkbench()
				.getSharedImages()
				.getImage(
						enabled ? org.eclipse.ui.ISharedImages.IMG_TOOL_DELETE
								: org.eclipse.ui.ISharedImages.IMG_TOOL_DELETE_DISABLED);
		if (menuItem != null) {
			menuItem.setImage(image);
			menuItem.setEnabled(enabled);
		}
		if (toolItem != null) {
			toolItem.setImage(image);
			toolItem.setEnabled(enabled);
		}
	}

	public void run() {

		final IHandlerService handlerService = (IHandlerService) viewSite
				.getService(IHandlerService.class);
		System.out.println(viewSite.getSelectionProvider());
		IEvaluationContext evaluationContext = handlerService
				.createContextSnapshot(true);
		ExecutionEvent event = new ExecutionEvent(null, Collections.EMPTY_MAP,
				null, evaluationContext);
		try {
			handler.execute(event);
		} catch (ExecutionException e) {
			FavoritesLog.logError(e);
		}
	}
}
