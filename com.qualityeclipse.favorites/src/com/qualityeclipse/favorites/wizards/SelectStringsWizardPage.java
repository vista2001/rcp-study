package com.qualityeclipse.favorites.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * The second page of the wizard contains a checkbox list of key/value pairs
 * that can be extracted from the source file. Rather than initializing its
 * contents when first created, this page updates its contents whenever it
 * becomes visible by overriding the setVisible() method.
 */
public class SelectStringsWizardPage extends WizardPage
{
   private CheckboxTableViewer checkboxTableViewer;
   private IPath sourceLocation;
   private ExtractedStringsModel stringModel;

   public SelectStringsWizardPage() {
      super("selectStrings");
      setTitle("Extract Strings");
      setDescription("Select the strings to be extracted");
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
      container.setLayout(new FormLayout());
      setControl(container);

      checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER);
      checkboxTableViewer.setContentProvider(new ExtractedStringsContentProvider());
      checkboxTableViewer.setLabelProvider(new ExtractedStringsLabelProvider());
      final Table table = checkboxTableViewer.getTable();
      final FormData formData = new FormData();
      formData.bottom = new FormAttachment(100, 0);
      formData.right = new FormAttachment(100, 0);
      formData.top = new FormAttachment(0, 0);
      formData.left = new FormAttachment(0, 0);
      table.setLayoutData(formData);
      table.setHeaderVisible(true);

      final TableColumn tableColumn = new TableColumn(table, SWT.NONE);
      tableColumn.setWidth(200);
      tableColumn.setText("Key");

      final TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
      tableColumn_1.setWidth(250);
      tableColumn_1.setText("Value");
   }

   /**
    * Update the content before becoming visible.
    */
   public void setVisible(boolean visible) {
      if (visible) {
         IPath location = ((ExtractStringsWizard) getWizard()).getSourceLocation();
         if (!location.equals(sourceLocation)) {
            sourceLocation = location;
            stringModel = new ExtractedStringsModel(sourceLocation);
            checkboxTableViewer.setInput(stringModel);
         }
      }
      super.setVisible(visible);
   }

   /**
    * Return the currently selected strings.
    */
   public ExtractedString[] getSelection() {
      Object[] checked = checkboxTableViewer.getCheckedElements();
      int count = checked.length;
      ExtractedString[] extracted = new ExtractedString[count];
      System.arraycopy(checked, 0, extracted, 0, count);
      return extracted;
   }
}
