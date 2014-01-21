package com.youtell.backchat.activities;

import com.youtell.backchat.Application;
import com.youtell.backchat.fragments.GabDetailFragment;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.Message;
import com.youtell.backchat.observers.GCMNotificationObserver;
import com.youtell.backchat.observers.GCMToastNotificationObserver;
import com.youtell.backchat.observers.GabObserver;
import com.youtell.backchat.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

public class BaseGabDetailActivity extends BaseActivity implements GabDetailFragment.Callbacks, GabObserver.Observer {
	public static final String ARG_GAB_ID = "ARG_GAB_ID";
	public static final String ARG_KEYBOARD_OPEN = "ARG_KEYBOARD_OPEN";

	protected static final int SHOW_DELETE_BUTTON = 0x1;
	protected static final int SHOW_ANON_BUTTONS = 0x10;
	protected static final int SHOW_CANCEL_BUTTON = 0x100;

	protected Gab gab;
	protected int gabID;
	
	private GabObserver observer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gab_detail);
		setupTitleActionBar();
		
		//TODO
		gabID = getIntent().getIntExtra(BaseGabDetailActivity.ARG_GAB_ID, -1);
		gab = Gab.getByID(gabID);
		
		observer = new GabObserver(this, gab);
		toastObserver.setWatchingGab(gab);
	}

	protected void setupFragment(int fromRes, int toRes, int buttonSendRes, int fromTextColor, int toTextColor) {
		setupFragment(fromRes, toRes, buttonSendRes, fromTextColor, toTextColor, getIntent().getBooleanExtra(BaseGabDetailActivity.ARG_KEYBOARD_OPEN, false));
	}

	protected void setupFragment(int fromRes, int toRes, int buttonSendRes, int fromTextColorRes, int toTextColorRes, boolean keyboardState)
	{
		Bundle arguments = new Bundle();
		arguments.putInt(GabDetailFragment.ARG_GAB_ID, gabID);
		arguments.putInt(GabDetailFragment.FROM_MESSAGE_RES, fromRes);
		arguments.putInt(GabDetailFragment.TO_MESSAGE_RES, toRes);
		arguments.putInt(GabDetailFragment.FROM_MESSAGE_COLOR_RES, fromTextColorRes);
		arguments.putInt(GabDetailFragment.TO_MESSAGE_COLOR_RES, toTextColorRes);
		arguments.putInt(GabDetailFragment.SEND_BUTTON_RES, buttonSendRes);
		GabDetailFragment fragment = new GabDetailFragment();
		fragment.setArguments(arguments);
		getFragmentManager().beginTransaction()
		.add(R.id.gab_detail_container, fragment)
		.commit();
		
		this.getWindow().setSoftInputMode(keyboardState ? WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE : WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
	}

	protected void setupActionBar(int flags) {
		//we have to custom layout the params because the custom view can't affect the parent in XML
		View cView = getLayoutInflater().inflate(R.layout.gab_detail_activity_bar_layout, null);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		getActionBar().setCustomView(cView, lp);

		cView.findViewById(R.id.gab_clue_button).setVisibility((flags & SHOW_ANON_BUTTONS) == SHOW_ANON_BUTTONS ? View.VISIBLE : View.GONE);
		cView.findViewById(R.id.gab_tag_button).setVisibility((flags & SHOW_ANON_BUTTONS) == SHOW_ANON_BUTTONS ? View.VISIBLE : View.GONE);		
		ImageButton deleteButton = (ImageButton) cView.findViewById(R.id.gab_delete_button);
		deleteButton.setVisibility((flags & SHOW_DELETE_BUTTON) == SHOW_DELETE_BUTTON ? View.VISIBLE : View.GONE);
		cView.findViewById(R.id.gab_cancel_button).setVisibility((flags & SHOW_CANCEL_BUTTON) == SHOW_CANCEL_BUTTON ? View.VISIBLE : View.GONE);		
	}

	@Override
	public void beforeMessageSend(Message message) {
		//do nothing.
	}        
	
	public void onDeleteClick(View v) {
		gab.remove();
		
		runOnNextScreen(this,
				new Runnable() {
					@Override
					public void run() {
						Toast toast = Toast.makeText(getApplicationContext(), 
								getResources().getText(R.string.gab_deleted_toast), Toast.LENGTH_SHORT);

						toast.show();							
					}
				}
			);
		
		goUp();
	}

	static public Intent getDetailIntent(Context context, Gab gab) {		
		Intent detailIntent = new Intent(context, getDetailIntentClass(gab));
		detailIntent.putExtra(BaseGabDetailActivity.ARG_GAB_ID, gab.getID());
		return detailIntent;
	}

	static public Class<? extends BaseGabDetailActivity> getDetailIntentClass(Gab gab) {
		Class<? extends BaseGabDetailActivity> classType;
		if(gab.isAnonymous())
			classType = GabAnonymousDetailActivity.class;
		else
			classType = GabDetailActivity.class;
		return classType;
	}
	
	@Override
	public void onItemSelected(Message item) {
		//never will be called.
	}
	
	@Override
	public void onResume()
	{		
		super.onResume();
		observer.startListening();
		gab.updateWithMessages();
	}
	
	@Override
	public void onStop()
	{
		observer.stopListening();
		super.onStop();
	}

	@Override
	public void onChange(String action, int gabID) {
		if(gab.getUnreadCount() != 0) {
			gab.setUnreadCount(0);
			gab.save();
			gab.updateUnread();
		}
		
		gab.refresh();
		setTitle(gab.getTitle());
	}

	@Override
	public void onMessageImageClick(Message message) {
		Intent imageIntent = new Intent(this, ViewImageActivity.class);
		imageIntent.putExtra(ViewImageActivity.IMAGE_URL, message.getImageRemotePath());
		startActivity(imageIntent);
	}
	
	@Override
	public void refresh() {
		setTitle(gab.getTitle());
	}
}
