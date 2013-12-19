package com.youtell.backchat.fragments;

import java.util.ArrayList;

import com.youtell.backchat.adapters.InviteCursorAdapter;
import com.youtell.backchat.R;

import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager;
import android.view.ActionMode;

public class InviteContactsFragment extends CallbackListFragment<InviteContactsFragment.Callbacks>
implements LoaderManager.LoaderCallbacks<Cursor>, InviteCursorAdapter.SelectionProvider 
{
	private InviteCursorAdapter adapter;
	private ActionMode cab = null;   

	public interface Callbacks {
		public void onInvite(ArrayList<Integer> contactIds);
	}

	// These are the Contacts rows that we will retrieve.
	private static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
		ContactsContract.CommonDataKinds.Phone._ID,
		ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
		ContactsContract.CommonDataKinds.Phone.NUMBER,
		ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
	};

	@Override 
	public void onActivityCreated(Bundle savedInstanceState)
	{		
		super.onActivityCreated(savedInstanceState);

		getListView().setItemsCanFocus(false);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		adapter = new InviteCursorAdapter(getActivity(), this);
		selectedIndices = new ArrayList<Integer>();

		setListAdapter(adapter);

		getLoaderManager().initLoader(0, null, this);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri baseUri;
		if (false) {//mCurFilter != null) {
			baseUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
					Uri.encode(null)); //TODO
		} else {
			baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		}

		String select = String.format("((%s NOTNULL) AND (%s NOTNULL) AND (%s != ''))",
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

		return new CursorLoader(getActivity(), baseUri,
				CONTACTS_SUMMARY_PROJECTION, select, null,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);

		selectAll();

		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		Integer idValue = Integer.valueOf((int)id);
		if(!selectedIndices.remove(idValue)) {
			selectedIndices.add(idValue);
		}

		if(selectedIndices.size() > 0)
			showCAB();
		else
			hideCAB();

		//TODO perf
		adapter.notifyDataSetChanged();
	}		

	private ArrayList<Integer> selectedIndices;

	@Override
	public boolean isSelected(int id) {
		return selectedIndices.contains(Integer.valueOf(id));
	}

	private void showCAB() {
		if(cab != null) {
			cab.invalidate();
			return;
		}

		cab = getActivity().startActionMode(new ActionMode.Callback() {			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				mode.setTitle(String.format("%d selected", selectedIndices.size()));
				boolean showSelectAll = selectedIndices.size() != adapter.getCount();
				menu.findItem(R.id.invite_contacts_contextual_select_all).setVisible(showSelectAll);

				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				//the user hit back
				cab = null;
				selectedIndices.clear();
				adapter.notifyDataSetChanged();
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.invite_contacts_contextual, menu);
				mode.setTitle(String.format("%d selected", selectedIndices.size()));
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				if(item.getItemId() == R.id.invite_contacts_contextual_invite) {
					onInvite();
				}
				else if(item.getItemId() == R.id.invite_contacts_contextual_select_all) {
					selectAll();
				}
				return true;
			}
		});
		
		customizeActionModeCloseButton();
	}

	private void customizeActionModeCloseButton() {
		int buttonId = Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android");    
		View v = getActivity().findViewById(buttonId);
		if (v == null)
			return;
		LinearLayout ll = (LinearLayout) v;
		if (ll.getChildCount() > 1 && ll.getChildAt(1) != null) { 
			TextView tv = (TextView) ll.getChildAt(1);
			tv.setText("");
		}
	}
	
	private void onInvite() 
	{
		if(selectedIndices.size() > 0 && mCallbacks != null)
			mCallbacks.onInvite(this.selectedIndices);
	}

	private void hideCAB() 
	{
		cab.finish();
		cab = null;
	}

	public void selectAll() {
		selectedIndices.clear();
		for(int i=0;i<adapter.getCount();i++) {
			selectedIndices.add(Integer.valueOf((int)adapter.getItemId(i)));
		}
		showCAB();
		adapter.notifyDataSetChanged();
	}
}
