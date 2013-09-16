package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;
import com.youtell.backdoor.dummy.DummyContent;
import com.youtell.backdoor.fragments.GabDetailFragment;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class BaseGabDetailActivity extends BaseActivity implements GabDetailFragment.Callbacks {
	public static final String ARG_GAB_ID = "ARG_GAB_ID";
	public static final String ARG_KEYBOARD_OPEN = "ARG_KEYBOARD_OPEN";

	protected static final int SHOW_DELETE_BUTTON = 0x1;
	protected static final int SHOW_ANON_BUTTONS = 0x10;
	protected static final int SHOW_CANCEL_BUTTON = 0x100;


	protected Gab gab;
	protected String gabID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gab_detail);
		gabID = getIntent().getStringExtra(BaseGabDetailActivity.ARG_GAB_ID);
		gab = DummyContent.ITEM_MAP.get(gabID);
		setTitle(gab.getTitle());
	}

	protected void setupFragment(int fromRes, int toRes) {
		setupFragment(fromRes, toRes, getIntent().getBooleanExtra(BaseGabDetailActivity.ARG_KEYBOARD_OPEN, false));
	}

	protected void setupFragment(int fromRes, int toRes, boolean keyboardState)
	{
		Bundle arguments = new Bundle();
		arguments.putString(GabDetailFragment.ARG_GAB_ID,
				gabID);
		arguments.putInt(GabDetailFragment.FROM_MESSAGE_RES, fromRes);
		arguments.putInt(GabDetailFragment.TO_MESSAGE_RES, toRes);
		arguments.putBoolean(GabDetailFragment.ARG_SHOW_KEYBOARD, keyboardState);
		GabDetailFragment fragment = new GabDetailFragment();
		fragment.setArguments(arguments);
		getFragmentManager().beginTransaction()
		.add(R.id.gab_detail_container, fragment)
		.commit();
	}

	protected void setupActionBar(int flags) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		//we have to custom layout the params because the custom view can't affect the parent in XML
		View cView = getLayoutInflater().inflate(R.layout.gab_detail_activity_bar_layout, null);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		actionBar.setCustomView(cView, lp);

		cView.findViewById(R.id.gab_clue_button).setVisibility((flags & SHOW_ANON_BUTTONS) == SHOW_ANON_BUTTONS ? View.VISIBLE : View.GONE);
		cView.findViewById(R.id.gab_tag_button).setVisibility((flags & SHOW_ANON_BUTTONS) == SHOW_ANON_BUTTONS ? View.VISIBLE : View.GONE);	
		cView.findViewById(R.id.gab_delete_button).setVisibility((flags & SHOW_DELETE_BUTTON) == SHOW_DELETE_BUTTON ? View.VISIBLE : View.GONE);
		cView.findViewById(R.id.gab_cancel_button).setVisibility((flags & SHOW_CANCEL_BUTTON) == SHOW_CANCEL_BUTTON ? View.VISIBLE : View.GONE);		
	}

	@Override
	public void onMessageSend(Message message) {
		//do nothing.
	}        

	public void onDeleteClick(View v) {
		gab.delete();
		goUp();
	}

	static public Intent getDetailIntent(Context context, Gab gab) {
		Class<? extends BaseGabDetailActivity> classType;
		if(gab.isAnonymous())
			classType = GabAnonymousDetailActivity.class;
		else
			classType = GabDetailActivity.class;
		Intent detailIntent = new Intent(context, classType);
		detailIntent.putExtra(BaseGabDetailActivity.ARG_GAB_ID, gab.getID());
		return detailIntent;
	}
}
