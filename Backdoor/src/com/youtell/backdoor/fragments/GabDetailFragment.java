package com.youtell.backdoor.fragments;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.youtell.backdoor.Application;
import com.youtell.backdoor.R;
import com.youtell.backdoor.adapters.GabDetailMessageAdapter;
import com.youtell.backdoor.models.DatabaseObject;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.MessageObserver;

/**
 * A fragment representing a single Gab detail screen.
 */
public class GabDetailFragment extends ListAdapterCallbackFragment<GabDetailMessageAdapter, MessageObserver, Message, GabDetailFragment.Callbacks> 
implements OnClickListener, GabDetailMessageAdapter.Callbacks {
	public static final String ARG_GAB_ID = "gab_id";

	public static final String FROM_MESSAGE_RES = "FROM_MESSAGE_RES";
	public static final String TO_MESSAGE_RES = "TO_MESSAGE_RES";
	public static final String FROM_MESSAGE_COLOR_RES = "FROM_MESSAGE_COLOR_RES";
	public static final String TO_MESSAGE_COLOR_RES = "TO_MESSAGE_COLOR_RES";
	public static final String SEND_BUTTON_RES = "SEND_BUTTON_RES";
	
	private EditText textInput;
	private Gab gab;
	
	private static final int PICK_IMAGE = 0;

	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Message> {
		public void beforeMessageSend(Message message);
		public void onMessageImageClick(Message message);
	}

	public GabDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		gab = Gab.getByID(getArguments().getInt(ARG_GAB_ID, -1)); //TODO
		super.onCreate(savedInstanceState);       
	}       

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_gab_detail, container, false);

		textInput = (EditText) view.findViewById(R.id.gab_input_text);

		int sendButtonRes = getArguments().getInt(SEND_BUTTON_RES);
		Button button = (Button) view.findViewById(R.id.gab_send_button);
		button.setBackgroundResource(sendButtonRes);
		button.setOnClickListener(this); 
		ImageButton i_button = (ImageButton)view.findViewById(R.id.gab_detail_camera_button);
		i_button.setOnClickListener(this);
	    
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedBundleState) {
		super.onActivityCreated(savedBundleState);
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
	}

	public void onClick(View v) {
		if(v.getId() == R.id.gab_send_button) {
			String text = textInput.getText().toString();
			if(text.trim().length() == 0)
				return;
			
			Message m = new Message();
			m.setText(text);
			sendMessage(m);
		}
		else if(v.getId() == R.id.gab_detail_camera_button) {
			Intent pickIntent = new Intent();
			pickIntent.setType("image/*");
			pickIntent.setAction(Intent.ACTION_GET_CONTENT);

			Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			String pickTitle = getResources().getString(R.string.gab_pick_image_description);
			Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
			chooserIntent.putExtra
			(
			  Intent.EXTRA_INITIAL_INTENTS, 
			  new Intent[] { takePhotoIntent }
			);

			startActivityForResult(chooserIntent, PICK_IMAGE);
		}
	}

	@Override
	public void onPause() {
		//TODO try to keep it open if it is open? causes the activity to black out for some reason (too slow?)
		InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		super.onPause();
	}

	@Override
	protected GabDetailMessageAdapter createAdapter() {
		int fromMessageRes = getArguments().getInt(FROM_MESSAGE_RES);
		int toMessageRes = getArguments().getInt(TO_MESSAGE_RES);
		int fromColor = getResources().getColor(getArguments().getInt(FROM_MESSAGE_COLOR_RES));
		int toColor = getResources().getColor(getArguments().getInt(TO_MESSAGE_COLOR_RES));
		return new GabDetailMessageAdapter(getActivity(), 
				gab, this, fromMessageRes, toMessageRes, fromColor, toColor);
	}

	@Override
	protected MessageObserver createObserver() {
		return new MessageObserver(new MessageObserver.Observer() {

			@Override
			public void onChange(String action, int gabID, int objectID) {
				if(adapter != null)
					adapter.notifyDataSetChanged();
			}

			@Override
			public void refresh() {
				if(adapter != null)
					adapter.notifyDataSetChanged();
			}
			
		}, gab);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(requestCode == PICK_IMAGE && data != null && data.getData() != null) {
	        Uri _uri = data.getData();

	        Cursor cursor = getActivity().getContentResolver().query(_uri, new String[] { 
	        		android.provider.MediaStore.Images.ImageColumns.DATA,
	        		android.provider.MediaStore.Images.ImageColumns.ORIENTATION
	        		}, null, null, null);
	        
	        cursor.moveToFirst();
	        final int dataColumn = cursor.getColumnIndex(android.provider.MediaStore.Images.ImageColumns.DATA);
	        final int orientationColumn = cursor.getColumnIndex(android.provider.MediaStore.Images.ImageColumns.ORIENTATION);
	        final String imageFilePath = cursor.getString(dataColumn);
	        final int degrees = cursor.getInt(orientationColumn);
	        
	        Log.e("IMAGE_PICKER", imageFilePath);
	        
	        Application.mixpanel.track("Selected Image", null);
	        
	        final File imageFile = new File(imageFilePath);
	        
            Message m = new Message();
			m.setFilePath(imageFile);
			sendMessage(m);
			
	        cursor.close();
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void sendMessage(Message m) {
		m.setMine(true);
		m.setCreatedAt(new Date());
		m.setRemoteID(DatabaseObject.NEW_OBJECT);
		
		if(mCallbacks != null) {
			mCallbacks.beforeMessageSend(m);
		}

		gab.addMessage(m);

		textInput.setText(null);
	}

	@Override
	public void onImageClick(Message which) {
		if(mCallbacks != null) {
			mCallbacks.onMessageImageClick(which);
		}
	}
	
}
