package com.qualityeclipse.favorites.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IMemento;

import com.qualityeclipse.favorites.model.FavoriteItemType;
import com.qualityeclipse.favorites.model.IFavoriteItem;

/**
 * A FavoritesView filter used to extract a subset of favorites items based upon
 * type matching.
 */
public class FavoritesViewTypeFilter extends ViewerFilter
{
   private static final String TAG_TYPE_ID = "typeId";
   private static final String TAG_TYPE = "TypeFilterInfo";

   /**
    * The viewer whose content is being filtered
    */
   private final StructuredViewer viewer;

   /**
    * A collection of types whose items should be displayed or <code>null</code>
    * if all items should be displayed.
    */
   private HashSet<FavoriteItemType> types;

   public FavoritesViewTypeFilter(StructuredViewer viewer) {
      this.viewer = viewer;
      this.types = null;
   }

   public FavoriteItemType[] getTypes() {
      if (types == null)
         return FavoriteItemType.getTypes();
      return types.toArray(new FavoriteItemType[types.size()]);
   }

   public void setTypes(FavoriteItemType[] selectedTypes) {
      FavoriteItemType[] allTypes = FavoriteItemType.getTypes();
      boolean filtering = types != null;
      if (selectedTypes.length < allTypes.length) {
         types = new HashSet<FavoriteItemType>();
         types.addAll(Arrays.asList(selectedTypes));
         if (!filtering)
            viewer.addFilter(this);
         else
            viewer.refresh();
      }
      else {
         types = null;
         if (filtering)
            viewer.removeFilter(this);
      }
   }

   public boolean select(Viewer viewer, Object parentElement, Object element) {
      return types == null || types.contains(((IFavoriteItem) element).getType());
   }

   public void saveState(IMemento memento) {
      if (types == null)
         return;
      IMemento mem = memento.createChild(TAG_TYPE);
      int index = 0;
      for (Iterator<FavoriteItemType> iter = types.iterator(); iter.hasNext();) {
         mem.putString(TAG_TYPE_ID + index, iter.next().getId());
         index++;
      }
   }

   public void init(IMemento memento) {
      FavoriteItemType[] allTypes = FavoriteItemType.getTypes();
      IMemento mem = memento.getChild(TAG_TYPE);
      if (mem == null) {
         setTypes(allTypes);
         return;
      }
      List<FavoriteItemType> someTypes = new ArrayList<FavoriteItemType>(allTypes.length);
      int index = 0;
      while (true) {
         String eachId = mem.getString(TAG_TYPE_ID + index);
         if (eachId == null)
            break;
         FavoriteItemType eachType = FavoriteItemType.getType(eachId);
         if (eachType != null)
            someTypes.add(eachType);
         index++;
      }
      setTypes(someTypes.toArray(new FavoriteItemType[someTypes.size()]));
   }
}