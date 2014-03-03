package com.qualityeclipse.favorites.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.qualityeclipse.favorites.dialogs.ResizableDialog;

/**
 * Open the example resizable dialog
 */
public class OpenResizableDialogHandler extends AbstractHandler
{
   public Object execute(ExecutionEvent event) throws ExecutionException {
      new ResizableDialog(HandlerUtil.getActiveShell(event)).open();
      return null;
   }
}
