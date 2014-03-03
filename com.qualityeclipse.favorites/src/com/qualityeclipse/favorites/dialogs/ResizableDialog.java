package com.qualityeclipse.favorites.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.qualityeclipse.favorites.FavoritesActivator;

/**
 * An example resizable dialog
 */
public class ResizableDialog extends Dialog
{
   private static final String RESIZABLE_DIALOG_SETTINGS = "MyResizableDialogSettings";

   public ResizableDialog(IShellProvider parentShell) {
      super(parentShell);
   }

   public ResizableDialog(Shell parentShell) {
      super(parentShell);
   }

   protected Control createDialogArea(Composite parent) {
      Composite composite = (Composite) super.createDialogArea(parent);
      new Label(composite, SWT.NONE).setText("This is an example resizable dialog");
      return composite;
   }

   /**
    * Override this method to return <code>true</code> so that the dialog will
    * be resizable.
    * 
    * @see org.eclipse.jface.dialogs.Dialog#isResizable()
    */
   protected boolean isResizable() {
      return true;
   }

   /**
    * Override this method to return the object that persists the dialog
    * location and size.
    * 
    * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
    */
   protected IDialogSettings getDialogBoundsSettings() {
      IDialogSettings settings = FavoritesActivator.getDefault().getDialogSettings();
      IDialogSettings section = settings.getSection(RESIZABLE_DIALOG_SETTINGS);
      if (section == null)
         section = settings.addNewSection(RESIZABLE_DIALOG_SETTINGS);
      return section;
   }
}
