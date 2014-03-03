package com.qualityeclipse.favorites.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.qualityeclipse.favorites.FavoritesActivator;
import com.qualityeclipse.favorites.dialogs.ExceptionDetailsDialog;

/**
 * Handler that opens the ExceptionDetailsDialog displaying a fake exception
 */
public class OpenExceptionDetailsDialogHandler extends AbstractHandler
{
   public Object execute(ExecutionEvent event) throws ExecutionException {
      Shell shell = HandlerUtil.getActiveShell(event);
      try {
         throw new RuntimeException("Fake Exception");
      }
      catch (Exception e) {
         new ExceptionDetailsDialog(shell, "Test",
               null, // image (defaults to error image)
               "This is a fake exception to test the "
                     + ExceptionDetailsDialog.class.getName() + " dialog.", e,
               FavoritesActivator.getDefault().getBundle()).open();
      }
      return null;
   }

}
