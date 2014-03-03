package com.qualityeclipse.favorites.wizards;

import org.eclipse.jface.viewers.*;

/**
 * The content provider second page of the extract strings wizard
 */
public class ExtractedStringsContentProvider
      implements IStructuredContentProvider
{
   // dummy data
   private static final Object[] items =
         {
               new ExtractedString("plugin_id", "com.qualityeclipse.favorites"),
               new ExtractedString("plugin_name", "Favorites Plug-in"),
               new ExtractedString("plugin_version", "1.0.0"),
               new ExtractedString("plugin_provider-name", "QualityEclipse"),
               new ExtractedString("plugin_class",
                     "com.qualityeclipse.favorites.FavoritesPlugin"),
               new ExtractedString("view_name", "Favorites"),
               new ExtractedString("view_icon", "icons/sample.gif"),
               new ExtractedString("view_category", "com.qualityeclipse.favorites"),
               new ExtractedString("view_class",
                     "com.qualityeclipse.favorites.views.FavoritesView"),
               new ExtractedString("view_id",
                     "com.qualityeclipse.favorites.views.FavoritesView"), };

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   public Object[] getElements(Object inputElement) {
      return items;
   }

   public void dispose() {
   }
}