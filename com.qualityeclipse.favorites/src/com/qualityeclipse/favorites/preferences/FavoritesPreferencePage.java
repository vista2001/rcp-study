package com.qualityeclipse.favorites.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.qualityeclipse.favorites.FavoritesActivator;

public class FavoritesPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private BooleanFieldEditor namePrefEditor;
	private BooleanFieldEditor locationPrefEditor;

	public FavoritesPreferencePage() {
		super(GRID);
		setPreferenceStore(FavoritesActivator.getDefault().getPreferenceStore());
		setDescription("Favorites view column visibility:");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		namePrefEditor = new BooleanFieldEditor(
				PreferenceConstants.FAVORITES_VIEW_NAME_COLUMN_VISIBLE,
				"Show name column", getFieldEditorParent());
		addField(namePrefEditor);
		locationPrefEditor = new BooleanFieldEditor(
				PreferenceConstants.FAVORITES_VIEW_LOCATION_COLUMN_VISIBLE,
				"Show location column", getFieldEditorParent());
		addField(locationPrefEditor);
	}

	protected void checkState() {
		super.checkState();
		if (!isValid())
			return;
		if (!namePrefEditor.getBooleanValue()
				&& !locationPrefEditor.getBooleanValue()) {
			setErrorMessage("Must have at least one column visible");
			setValid(false);
		} else {
			setErrorMessage(null);
			setValid(true);
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getProperty().equals(FieldEditor.VALUE)) {
			if (event.getSource() == namePrefEditor
					|| event.getSource() == locationPrefEditor)
				checkState();
		}
	}

}
