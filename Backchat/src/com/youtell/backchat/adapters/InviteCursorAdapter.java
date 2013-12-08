package com.youtell.backchat.adapters;

import com.youtell.backchat.models.Contact;
import com.youtell.backchat.tiles.ContactTile;
import com.youtell.backchat.tiles.Tile;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class InviteCursorAdapter extends CursorAdapter {
	public interface SelectionProvider {
		public boolean isSelected(int id);
	}
	
	private SelectionProvider selectionProvider;
	
	public InviteCursorAdapter(Context context, SelectionProvider selectionProvider) {
		super(context, null, 0);
		this.selectionProvider = selectionProvider;	
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Tile tile = new ContactTile(context, view);
		
		//TODO too tightly bound to cursor..?
		int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		int indexID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
		int indexPhoto = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
		
		Contact c = new Contact();
		c.name = cursor.getString(indexName);
		c.number = cursor.getString(indexNumber);
		c.photoURI = cursor.getString(indexPhoto);
		c.isSelected = selectionProvider.isSelected(cursor.getInt(indexID));
		
		tile.fill(c);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Tile tile = new ContactTile(context, parent);
		View views = tile.getViews();
		return views;
	}

}
