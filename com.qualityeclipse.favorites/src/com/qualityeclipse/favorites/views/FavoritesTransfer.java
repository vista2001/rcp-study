package com.qualityeclipse.favorites.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import com.qualityeclipse.favorites.model.FavoritesManager;
import com.qualityeclipse.favorites.model.IFavoriteItem;

public class FavoritesTransfer extends ByteArrayTransfer {
	private static final FavoritesTransfer INSTANCE = new FavoritesTransfer();
	private static final String TYPE_NAME = "favorites-transfer-format:"
			+ System.currentTimeMillis() + ":" + INSTANCE.hashCode();
	private static final int TYPEID = registerType(TYPE_NAME);

	public static FavoritesTransfer getInstance() {
		return INSTANCE;
	}


	private FavoritesTransfer() {
		super();
	}

	@Override
	protected int[] getTypeIds() {
		 return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		 return new String[] { TYPE_NAME };
	}
	 protected void javaToNative(Object data, TransferData transferData) {

	      if (!(data instanceof IFavoriteItem[]))
	         return;
	      IFavoriteItem[] items = (IFavoriteItem[]) data;

	      /**
	       * The serialization format is: (int) number of items Then, the following
	       * for each item: (String) the type of item (String) the item-specific
	       * info glob
	       */

	      try {
	         ByteArrayOutputStream out = new ByteArrayOutputStream();
	         DataOutputStream dataOut = new DataOutputStream(out);
	         dataOut.writeInt(items.length);
	         for (int i = 0; i < items.length; i++) {
	            IFavoriteItem item = items[i];
	            dataOut.writeUTF(item.getType().getId());
	            dataOut.writeUTF(item.getInfo());
	         }
	         dataOut.close();
	         out.close();
	         super.javaToNative(out.toByteArray(), transferData);
	      }
	      catch (IOException e) {
	         // Send nothing if there were problems.
	      }
	   }

	   /**
	    * Converts a platform-specific representation of data to a Java
	    * representation.
	    * 
	    * @param transferData
	    *       the platform-specific representation of the data to be converted
	    * @return a java representation of the converted data if the conversion was
	    *    successful, else <code>null</code>
	    * @see org.eclipse.swt.dnd.Transfer
	    *    #nativeToJava(org.eclipse.swt.dnd.TransferData)
	    */
	   protected Object nativeToJava(TransferData transferData) {

	      /**
	       * The serialization format is: <br>
	       * (int) number of items <br>
	       * Then, the following for each item: <br>
	       * (String) the type of item <br>
	       * (String) the item-specific info glob
	       */

	      byte[] bytes = (byte[]) super.nativeToJava(transferData);
	      if (bytes == null)
	         return null;
	      DataInputStream in =
	            new DataInputStream(new ByteArrayInputStream(bytes));
	      try {
	         FavoritesManager mgr = FavoritesManager.getManager();
	         int count = in.readInt();
	         List<IFavoriteItem> items =
	               new ArrayList<IFavoriteItem>(count);
	         for (int i = 0; i < count; i++) {
	            String typeId = in.readUTF();
	            String info = in.readUTF();
	            items.add(mgr.newFavoriteFor(typeId, info));
	         }
	         return items.toArray(new IFavoriteItem[items.size()]);
	      }
	      catch (IOException e) {
	         return null;
	      }
	   }
}