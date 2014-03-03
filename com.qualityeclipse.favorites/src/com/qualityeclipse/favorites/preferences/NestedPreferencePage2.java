package com.qualityeclipse.favorites.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class NestedPreferencePage2 extends PreferencePage
      implements IWorkbenchPreferencePage
{

   public NestedPreferencePage2() {
   }

   public NestedPreferencePage2(String title) {
      super(title);
   }

   public NestedPreferencePage2(String title, ImageDescriptor image) {
      super(title, image);
   }

   @Override
   protected Control createContents(Composite parent) {
      Composite composite = new Composite(parent, SWT.NULL);
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      composite.setLayout(layout);
      composite.setFont(parent.getFont());
      new Label(composite, SWT.NONE).setText("A nested preference page two levels deep");
      return composite;
   }

   public void init(IWorkbench workbench) {

   }

}
