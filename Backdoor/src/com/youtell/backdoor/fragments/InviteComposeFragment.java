package com.youtell.backdoor.fragments;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostInviteRequest;
import com.youtell.backdoor.models.Contact;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.services.APIService;
import com.youtell.backdoor.tiles.ContactTile;
import com.youtell.backdoor.tiles.FriendTile;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

//TODO button enable disable text empty also send should fail unless loader done
public class InviteComposeFragment extends CallbackFragment<InviteComposeFragment.Callbacks> 
implements OnClickListener, OnCheckedChangeListener, LoaderManager.LoaderCallbacks<Cursor> {
	public interface Callbacks {
		public void afterSend();
	}

	public static final String ARG_CONTACT_IDS = "ARG_CONTACT_IDS";

	private EditText textInput;
	private Switch anonSwitch;
	private ArrayList<Integer> contactIDs;
	private Button sendButton;
	private ArrayList<String> numbers;
	private LinearLayout singleToRow;
	private HorizontalScrollView multipleToScroll;
	private LinearLayout multipleToLayout;
	private ContactTile contactTile;
	private boolean singleMode;
	
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

		singleToRow = (LinearLayout) view.findViewById(R.id.invite_compose_single_to_row);
		multipleToScroll = (HorizontalScrollView) view.findViewById(R.id.invite_compose_multiple_to_scroll);
		multipleToLayout = (LinearLayout) view.findViewById(R.id.invite_compose_multiple_to_row);
		getLoaderManager().initLoader(0, null, this);

		sendButton.setEnabled(false);

		setInviteText();
		
		singleMode = contactIDs.size() == 1;
		if(singleMode) {
			singleToRow.setVisibility(View.INVISIBLE);
			multipleToScroll.setVisibility(View.GONE);
			
			contactTile = new ContactTile(this.getActivity(), (View)singleToRow);
			contactTile.setShowSelected(false);
		}
		else {
			singleToRow.setVisibility(View.GONE);
			multipleToScroll.setVisibility(View.INVISIBLE);
		}
			
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		setInviteText();
	}

	@Override 
	public void onStop() {
		super.onStop();
	}
	
	private void setInviteText()
	{
		String personalizedURL = "http://bkdr.me";
		String username = User.getCurrentUser().getFullName();
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
		ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
		ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
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
		int indexPhoto = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
		int indexName = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

		Contact firstContact = null;
		
		while(data.moveToNext()) {			
			long id = data.getLong(indexID);
			if(contactIDs.contains(Integer.valueOf((int)id))) {
				String number;
				number = data.getString(indexPhone);
				numbers.add(number);
				

				if(firstContact == null) {
					firstContact = new Contact();
					//TODO too tightly bound to cursor..? c&p from invitecursoradapter					
					firstContact.name = data.getString(indexName);
					firstContact.number = data.getString(indexPhone);
					firstContact.photoURI = data.getString(indexPhoto);
					firstContact.isSelected = false;
				}
				
				if(!singleMode) {
					//TODO refactor out into an avatarview 
					ImageView image = new ImageView(this.getActivity());
					
					int rounding = (int)getActivity().getResources().getDimension(R.dimen.tile_avatar_rounding);
					DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)				
					.displayer(new RoundedBitmapDisplayer(rounding))
					.build();
					
					int size = (int) getActivity().getResources().getDimension(R.dimen.tile_avatar_size);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
					int margin = (int)getActivity().getResources().getDimension(R.dimen.tile_avatar_margin);
					lp.setMargins(0, margin, margin, margin);
					image.setLayoutParams(lp);
			
					String uri = data.getString(indexPhoto);
					if(uri != null)
						ImageLoader.getInstance().displayImage(uri, image, options);
					else
						image.setImageResource(R.drawable.anonymous_avatar);
					
					multipleToLayout.addView(image);

				}
			}
		}

		if(singleMode) {
			contactTile.fill(firstContact);
			singleToRow.setVisibility(View.VISIBLE);
		}
		else {
			multipleToScroll.setVisibility(View.VISIBLE);
		}
		
		Log.v("InviteComposer", String.format("%d numbers", numbers.size()));
		sendButton.setEnabled(true);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		sendButton.setEnabled(false);
	}

}
