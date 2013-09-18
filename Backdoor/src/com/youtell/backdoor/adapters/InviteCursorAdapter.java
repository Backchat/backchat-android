package com.youtell.backdoor.adapters;

import com.youtell.backdoor.Tile;

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
		Tile tile = new Tile(context, view);
		
		//TODO too tightly bound to cursor..?
		int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		int indexID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
		int indexPhoto = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
		String name = cursor.getString(indexName);
		String number = cursor.getString(indexNumber);
		int id = cursor.getInt(indexID);
		String photoURI = cursor.getString(indexPhoto);

		tile.fillWithContact(name, number, photoURI, selectionProvider.isSelected(id));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Tile tile = new Tile(context, parent);
		View views = tile.getViews();
		return views;
	}

}
