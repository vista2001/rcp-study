package com.qualityeclipse.favorites.wizards;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The first page of the Extract Strings wizard displays Source File and
 * Destination File text fields, each with a browse button to the right.
 */
public class SelectFilesWizardPage extends WizardPage
{
   private Text sourceFileField;
   private Text destinationFileField;
   private IPath initialSourcePath;

   public SelectFilesWizardPage() {
      super("selectFiles");
      setTitle("Select files");
      setDescription("Select the source and destination files");
   }

   /**
    * Creates the top level control for this dialog page under the given parent
    * composite, then calls <code>setControl</code> so that the created control
    * can be accessed via <code>getControl</code>
    * 
    * @param parent
    *           the parent composite
    */
   public void createControl(Composite parent) {
      Composite container = new Composite(parent, SWT.NULL);
      final GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      container.setLayout(gridLayout);
      setControl(container);

      final Label label = new Label(container, SWT.NONE);
      final GridData gridData = new GridData();
      gridData.horizontalSpan = 3;
      label.setLayoutData(gridData);
      label.setText("Select the plugin.xml file "
            + "from which strings will be extracted.");

      final Label label_1 = new Label(container, SWT.NONE);
      final GridData gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
      label_1.setLayoutData(gridData_1);
      label_1.setText("Source File:");

      sourceFileField = new Text(container, SWT.BORDER);
      sourceFileField.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            updatePageComplete();
         }
      });
      sourceFileField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      final Button button = new Button(container, SWT.NONE);
      button.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            browseForSourceFile();
         }
      });
      button.setText("Browse...");

      final Label label_2 = new Label(container, SWT.NONE);
      final GridData gridData_2 = new GridData();
      gridData_2.horizontalSpan = 3;
      label_2.setLayoutData(gridData_2);

      final Label label_3 = new Label(container, SWT.NONE);
      final GridData gridData_3 = new GridData();
      gridData_3.horizontalSpan = 3;
      label_3.setLayoutData(gridData_3);
      label_3.setText("Select the plugin.properties file "
            + "into which strings will be placed.");

      final Label label_4 = new Label(container, SWT.NONE);
      final GridData gridData_4 = new GridData();
      gridData_4.horizontalIndent = 20;
      label_4.setLayoutData(gridData_4);
      label_4.setText("Destination File:");

      destinationFileField = new Text(container, SWT.BORDER);
      destinationFileField.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            updatePageComplete();
         }
      });
      destinationFileField.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

      final Button button_1 = new Button(container, SWT.NONE);
      button_1.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            browseForDestinationFile();
         }
      });
      button_1.setText("Browse...");

      initContents();
   }

   /**
    * Called by the wizard to initialize the receiver's cached selection.
    * 
    * @param selection
    *           the selection or <code>null</code> if none
    */
   public void init(IStructuredSelection selection) {
      if (selection == null)
         return;

      // Find the first plugin.xml file.
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         Object item = iter.next();
         if (item instanceof IJavaElement) {
            IJavaElement javaElem = (IJavaElement) item;
            try {
               item = javaElem.getUnderlyingResource();
            }
            catch (JavaModelException e) {
               // Log and report the exception.
               e.printStackTrace();
               continue;
            }
         }
         if (item instanceof IFile) {
            IFile file = (IFile) item;
            if (file.getName().equals("plugin.xml")) {
               initialSourcePath = file.getLocation();
               break;
            }
            item = file.getProject();
         }
         if (item instanceof IProject) {
            IFile file = ((IProject) item).getFile("plugin.xml");
            if (file.exists()) {
               initialSourcePath = file.getLocation();
               break;
            }
         }
      }
   }

   /**
    * Called by <code>createControl</code> to initialize the receiver's content
    * based upon the cached selection provided by the wizard.
    */
   private void initContents() {
      if (initialSourcePath == null) {
         setPageComplete(false);
         return;
      }
      IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
      IPath path = initialSourcePath;
      if (rootLoc.isPrefixOf(path))
         path = path.setDevice(null).removeFirstSegments(rootLoc.segmentCount());
      sourceFileField.setText(path.toString());
      destinationFileField.setText(path.removeLastSegments(1)
            .append("plugin.properties")
            .toString());
      updatePageComplete();
   }

   /**
    * Update the current page complete state based on the field content.
    */
   private void updatePageComplete() {
      setPageComplete(false);

      IPath sourceLoc = getSourceLocation();
      if (sourceLoc == null || !sourceLoc.toFile().exists()) {
         setMessage(null);
         setErrorMessage("Please select an existing plugin.xml file");
         return;
      }

      IPath destinationLoc = getDestinationLocation();
      if (destinationLoc == null) {
         setMessage(null);
         setErrorMessage("Please specify a plugin.properties file"
               + " to contain the extracted strings");
         return;
      }

      setPageComplete(true);

      IPath sourceDirPath = sourceLoc.removeLastSegments(1);
      IPath destinationDirPath = destinationLoc.removeLastSegments(1);
      if (!sourceDirPath.equals(destinationDirPath)) {
         setErrorMessage(null);
         setMessage("The plugin.properties file is typically"
               + " located in the same directory" + " as the plugin.xml file", WARNING);
         return;
      }

      if (!destinationLoc.lastSegment().equals("plugin.properties")) {
         setErrorMessage(null);
         setMessage("The destination file is typically" + " named plugin.properties",
               WARNING);
         return;
      }

      setMessage(null);
      setErrorMessage(null);
   }

   /**
    * Open a file browser dialog to locate a source file
    */
   protected void browseForSourceFile() {
      IPath path = browse(getSourceLocation(), false);
      if (path == null)
         return;
      IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
      if (rootLoc.isPrefixOf(path))
         path = path.setDevice(null).removeFirstSegments(rootLoc.segmentCount());
      sourceFileField.setText(path.toString());
   }

   /**
    * Open a file browser dialog to locate a destination file
    */
   protected void browseForDestinationFile() {
      IPath path = browse(getDestinationLocation(), false);
      if (path == null)
         return;
      IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
      if (rootLoc.isPrefixOf(path))
         path = path.setDevice(null).removeFirstSegments(rootLoc.segmentCount());
      destinationFileField.setText(path.toString());
   }

   /**
    * Open a file dialog for selecting a file
    * 
    * @param path
    *           the initially selected file
    * @param mustExist
    *           <code>true</code> if the selected file must already exist, else
    *           <code>false</code>
    * @return the newly selected file or <code>null</code>
    */
   private IPath browse(IPath path, boolean mustExist) {
      FileDialog dialog = new FileDialog(getShell(), mustExist ? SWT.OPEN : SWT.SAVE);
      if (path != null) {
         if (path.segmentCount() > 1)
            dialog.setFilterPath(path.removeLastSegments(1).toOSString());
         if (path.segmentCount() > 0)
            dialog.setFileName(path.lastSegment());
      }
      String result = dialog.open();
      if (result == null)
         return null;
      return new Path(result);
   }

   /**
    * Answer the source file location or <code>null</code> if unspecified
    */
   public IPath getSourceLocation() {
      String text = sourceFileField.getText().trim();
      if (text.length() == 0)
         return null;
      IPath path = new Path(text);
      if (!path.isAbsolute())
         path = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path);
      return path;
   }

   /**
    * Answer the destination file location or <code>null</code> if unspecified
    */
   public IPath getDestinationLocation() {
      String text = destinationFileField.getText().trim();
      if (text.length() == 0)
         return null;
      IPath path = new Path(text);
      if (!path.isAbsolute())
         path = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path);
      return path;
   }
}
