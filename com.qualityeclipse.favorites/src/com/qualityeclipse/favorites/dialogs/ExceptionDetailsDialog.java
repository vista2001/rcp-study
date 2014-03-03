package com.qualityeclipse.favorites.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Dictionary;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;


public class ExceptionDetailsDialog extends AbstractDetailsDialog {
	 private final Object details;
	 private final Bundle bundle;
	 
	@Override
	protected Control createDetailsArea(Composite parent) {

	      // Create the details area.
	      Composite panel = new Composite(parent, SWT.NONE);
	      panel.setLayoutData(new GridData(GridData.FILL_BOTH));
	      GridLayout layout = new GridLayout();
	      layout.marginHeight = 0;
	      layout.marginWidth = 0;
	      panel.setLayout(layout);

	      // Create the details content.
	      createProductInfoArea(panel);
	      createDetailsViewer(panel);

	      return panel;
	   }

	public ExceptionDetailsDialog(Shell parentShell, String title, Image image,
	         String message, Object details, Bundle bundle) {
	      this(new SameShellProvider(parentShell), title, image, message, details, bundle);
	   }
	public ExceptionDetailsDialog(IShellProvider parentShell, String title, Image image,
	         String message, Object details, Bundle bundle) {
	      super(parentShell, getTitle(title, details), getImage(image, details), getMessage(
	            message, details));

	      this.details = details;
	      this.bundle = bundle;
	   }
	protected Composite createProductInfoArea(Composite parent) {

	      // If no bundle specified, then nothing to display here
	      if (bundle == null)
	         return null;

	      Composite composite = new Composite(parent, SWT.NULL);
	      composite.setLayoutData(new GridData());
	      GridLayout layout = new GridLayout();
	      layout.numColumns = 2;
	      layout.marginWidth =
	            convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
	      composite.setLayout(layout);

	      Dictionary<?, ?> bundleHeaders = bundle.getHeaders();
	      String pluginId = bundle.getSymbolicName();
	      String pluginVendor = (String) bundleHeaders.get("Bundle-Vendor");
	      String pluginName = (String) bundleHeaders.get("Bundle-Name");
	      String pluginVersion = (String) bundleHeaders.get("Bundle-Version");

	      new Label(composite, SWT.NONE).setText("Provider:");
	      new Label(composite, SWT.NONE).setText(pluginVendor);
	      new Label(composite, SWT.NONE).setText("Plug-in Name:");
	      new Label(composite, SWT.NONE).setText(pluginName);
	      new Label(composite, SWT.NONE).setText("Plug-in ID:");
	      new Label(composite, SWT.NONE).setText(pluginId);
	      new Label(composite, SWT.NONE).setText("Version:");
	      new Label(composite, SWT.NONE).setText(pluginVersion);

	      return composite;
	   }
	protected Control createDetailsViewer(Composite parent) {
	      if (details == null)
	         return null;

	      Text text =
	            new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL
	                  | SWT.V_SCROLL);
	      text.setLayoutData(new GridData(GridData.FILL_BOTH));

	      // Create the content.
	      StringWriter writer = new StringWriter(1000);
	      if (details instanceof Throwable)
	         appendException(new PrintWriter(writer), (Throwable) details);
	      else if (details instanceof IStatus)
	         appendStatus(new PrintWriter(writer), (IStatus) details, 0);
	      text.setText(writer.toString());

	      return text;
	   }
	 public static String getTitle(String title, Object details) {
	      if (title != null)
	         return title;
	      if (details instanceof Throwable) {
	         Throwable e = (Throwable) details;
	         while (e instanceof InvocationTargetException)
	            e = ((InvocationTargetException) e).getTargetException();
	         String name = e.getClass().getName();
	         return name.substring(name.lastIndexOf('.') + 1);
	      }
	      return "Exception";
	   }
	 public static Image getImage(Image image, Object details) {
	      if (image != null)
	         return image;
	      Display display = Display.getCurrent();
	      if (details instanceof IStatus) {
	         switch (((IStatus) details).getSeverity()) {
	         case IStatus.ERROR:
	            return display.getSystemImage(SWT.ICON_ERROR);
	         case IStatus.WARNING:
	            return display.getSystemImage(SWT.ICON_WARNING);
	         case IStatus.INFO:
	            return display.getSystemImage(SWT.ICON_INFORMATION);
	         case IStatus.OK:
	            return null;
	         }
	      }
	      return display.getSystemImage(SWT.ICON_ERROR);
	   }
	 public static String getMessage(String message, Object details) {
	      if (details instanceof Throwable) {
	         Throwable e = (Throwable) details;
	         while (e instanceof InvocationTargetException)
	            e = ((InvocationTargetException) e).getTargetException();
	         if (message == null)
	            return e.toString();
	         return MessageFormat.format(message, new Object[] { e.toString() });
	      }
	      if (details instanceof IStatus) {
	         String statusMessage = ((IStatus) details).getMessage();
	         if (message == null)
	            return statusMessage;
	         return MessageFormat.format(message, new Object[] { statusMessage });
	      }
	      if (message != null)
	         return message;
	      return "An Exception occurred.";
	   }

	   public static void appendException(PrintWriter writer, Throwable ex) {
	      if (ex instanceof CoreException) {
	         appendStatus(writer, ((CoreException) ex).getStatus(), 0);
	         writer.println();
	      }
	      appendStackTrace(writer, ex);
	      if (ex instanceof InvocationTargetException)
	         appendException(writer, ((InvocationTargetException) ex).getTargetException());
	   }

	   public static void appendStatus(PrintWriter writer, IStatus status, int nesting) {
	      for (int i = 0; i < nesting; i++)
	         writer.print("  ");
	      writer.println(status.getMessage());
	      IStatus[] children = status.getChildren();
	      for (int i = 0; i < children.length; i++)
	         appendStatus(writer, children[i], nesting + 1);
	   }

	   public static void appendStackTrace(PrintWriter writer, Throwable ex) {
	      ex.printStackTrace(writer);
	   }
}
