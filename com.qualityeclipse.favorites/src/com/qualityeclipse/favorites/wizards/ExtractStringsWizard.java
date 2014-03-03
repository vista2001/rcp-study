package com.qualityeclipse.favorites.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.qualityeclipse.favorites.FavoritesActivator;
import com.qualityeclipse.favorites.FavoritesLog;

/**
 * A two-page wizard for extracting strings from a plugin.xml file and placing
 * those strings into a separate plugin.properties file as specified by the RFRS
 * requirements. The wizard is responsible for instantiating the two pages,
 * facilitating communication from the first page to the second, and gathering
 * information from the two pages and performing the operation when the user
 * presses the Finish button.
 */
public class ExtractStringsWizard extends Wizard
      implements INewWizard
{
   private IStructuredSelection initialSelection;

   private SelectFilesWizardPage selectFilesPage;
   private SelectStringsWizardPage selectStringsPage;

   /**
    * Construct a new instance and initialize the dialog settings for this
    * instance.
    */
   public ExtractStringsWizard() {
      IDialogSettings favoritesSettings =
            FavoritesActivator.getDefault().getDialogSettings();
      IDialogSettings wizardSettings =
            favoritesSettings.getSection("ExtractStringsWizard");
      if (wizardSettings == null)
         wizardSettings = favoritesSettings.addNewSection("ExtractStringsWizard");
      setDialogSettings(favoritesSettings);
   }

   /**
    * Initializes this creation wizard using the passed workbench and object
    * selection. This method is called after the no argument constructor and
    * before other methods are called.
    * 
    * @param workbench
    *           the current workbench
    * @param selection
    *           the current object selection
    */
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      initialSelection = selection;
   }

   public void addPages() {
      setWindowTitle("Extract");
      selectFilesPage = new SelectFilesWizardPage();
      addPage(selectFilesPage);
      selectStringsPage = new SelectStringsWizardPage();
      addPage(selectStringsPage);
      selectFilesPage.init(initialSelection);
   }

   /**
    * This method is called by the wizard framework when the user presses the
    * Finish button.
    */
   public boolean performFinish() {
      final ExtractedString[] extracted = selectStringsPage.getSelection();

      // Perform the operation in a separate thread
      // so that the operation can be canceled.
      try {
         getContainer().run(true, true, new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                  InterruptedException {
               performOperation(extracted, monitor);
            }
         });
      }
      catch (InvocationTargetException e) {
         FavoritesLog.logError(e);
         return false;
      }
      catch (InterruptedException e) {
         // User canceled, so stop but don’t close wizard.
         return false;
      }
      return true;
   }

   /**
    * Called by the performFinish method on a separate thread to extract strings
    * from the source file.
    * @param extracted 
    * @param monitor
    *           the progress monitor
    */
   private void performOperation(ExtractedString[] extracted, IProgressMonitor monitor) throws InterruptedException {
      monitor.beginTask("Extracting Strings", extracted.length);
      for (int i = 0; i < extracted.length; i++) {
         // Replace sleep with actual work
         Thread.sleep(1000);
         if (monitor.isCanceled())
            throw new InterruptedException("Canceled by user");
         monitor.worked(1);
      }
      monitor.done();
   }

   /**
    * Answer the selected source location
    */
   public IPath getSourceLocation() {
      return selectFilesPage.getSourceLocation();
   }

   /**
    * Answer the selected destination location
    */
   public IPath getDestinationLocation() {
      return selectFilesPage.getDestinationLocation();
   }
}
