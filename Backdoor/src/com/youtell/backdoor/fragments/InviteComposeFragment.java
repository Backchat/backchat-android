package com.youtell.backdoor.fragments;

import java.util.ArrayList;

import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostInviteRequest;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.services.APIService;
import com.youtell.backdoor.observers.UserObserver;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;

//TODO button enable disable text empty also send should fail unless loader done
public class InviteComposeFragment extends CallbackFragment<InviteComposeFragment.Callbacks> 
implements OnClickListener, OnCheckedChangeListener, LoaderManager.LoaderCallbacks<Cursor>,
UserObserver.Observer {
	public interface Callbacks {
		public void afterSend();
	}

	public static final String ARG_CONTACT_IDS = "ARG_CONTACT_IDS";

	private EditText textInput;
	private Switch anonSwitch;
	private ArrayList<Integer> contactIDs;
	private Button sendButton;
	private ArrayList<String> numbers;

	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		contactIDs = getArguments().getIntegerArrayList(ARG_CONTACT_IDS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_invite_compose, container, false);

		textInput = (EditText) view.findViewById(R.id.invite_compose_text);
		sendButton = (Button) view.findViewById(R.id.invite_compose_send);
		sendButton.setOnClickListener(this);
		anonSwitch = (Switch)view.findViewById(R.id.invite_compose_anonymous_switch);
		anonSwitch.setOnCheckedChangeListener(this);

		getLoaderManager().initLoader(0, null, this);

		sendButton.setEnabled(false);

		return view;
	}

	UserObserver userObserver = new UserObserver(this);

	@Override
	public void onResume() {
		super.onResume();
		userObserver.startListening();
		setInviteText();
	}

	@Override 
	public void onStop() {
		super.onStop();
		userObserver.stopListening();
	}
	
	private void setInviteText()
	{
		String personalizedURL = "http://bkdr.me";
		String username = user.getFullName();
		int stringID;

		if(anonSwitch.isChecked()) 
			stringID = R.string.invite_compose_anon_text;
		else
			stringID = R.string.invite_compose_nonanon_text;

		final String fullInviteString = String.format("%s %s", 
				String.format(getActivity().getResources().getString(stringID), username),
				personalizedURL);

		textInput.setText(fullInviteString);

	}

	@Override
	public void onClick(View v) {

		sendInvite();
	}

	public void sendInvite() {
		String composeText = textInput.getText().toString();			
		APIService.fire(new PostInviteRequest(numbers, composeText));
		if(mCallbacks != null) {
			mCallbacks.afterSend();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		setInviteText();
	}

	private static final String[] CONTACTS_PROJECTION = new String[] {
		ContactsContract.CommonDataKinds.Phone._ID,
		ContactsContract.CommonDataKinds.Phone.NUMBER,
	};

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri baseUri;
		baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		return new CursorLoader(getActivity(), baseUri,
				CONTACTS_PROJECTION, null, null, null);
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		numbers = new ArrayList<String>();
		int indexID = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
		int indexPhone = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		while(data.moveToNext()) {			
			long id = data.getLong(indexID);
			if(contactIDs.contains(Integer.valueOf((int)id))) {
				String number;
				number = data.getString(indexPhone);
				numbers.add(number);
			}
		}

		Log.v("InviteComposer", String.format("%d numbers", numbers.size()));
		sendButton.setEnabled(true);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		sendButton.setEnabled(false);
	}

	@Override
	public void onUserChanged() {
		setInviteText();
	}

}
