package com.youtell.backdoor.activities;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.squareup.otto.Subscribe;
import com.youtell.backdoor.R;
import com.youtell.backdoor.fragments.GabListFragment;
import com.youtell.backdoor.fragments.SettingsMenuFragment;
import com.youtell.backdoor.gcm.GCM;
import com.youtell.backdoor.models.DBClosedEvent;
import com.youtell.backdoor.models.Database;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.GCMNotificationObserver;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.observers.UserObserver.Observer;
import com.youtell.backdoor.services.ORMUpdateService;

public class GabListActivity extends SlidingActivity implements GabListFragment.Callbacks, SettingsMenuFragment.Callbacks, GCM.Callbacks, Observer, 
GCMNotificationObserver.Observer {
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private Object userObserver;
	private GCMNotificationObserver gcmNotifications;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userObserver = UserObserver.registerObserver(this);

		setContentView(R.layout.activity_gab_list);

		GabListFragment fragment = new GabListFragment();
		getFragmentManager().beginTransaction()
		.add(R.id.gab_list, fragment)
		.commit();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		View cView = getLayoutInflater().inflate(R.layout.gab_list_activity_bar_layout, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT);
		actionBar.setCustomView(cView, lp);      

		int ID_MENUFRAME = 1001110101;
		FrameLayout frameLayout = new FrameLayout(this);
		frameLayout.setId(ID_MENUFRAME);
		setBehindContentView(frameLayout);
		android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
		SettingsMenuFragment menuFragment = new SettingsMenuFragment();
		ft.replace(ID_MENUFRAME, menuFragment);
		ft.commit();

		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.sliding_menu_offset);
		sm.setShadowWidthRes(R.dimen.sliding_menu_shadow_width);
		sm.setShadowDrawable(R.drawable.sliding_menu_shadow);

		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		
		gcmNotifications = new GCMNotificationObserver(this, null);
	}

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	public void settingsClick(View v) {
		getSlidingMenu().toggle();
	}

	public void newGabClick(View v) {
		Intent intent = new Intent(this, NewGabActivity.class);
		startActivity(intent);
	}

	public void inviteClick(View v) {
		Intent intent = new Intent(this, InviteContactsActivity.class);
		startActivity(intent);
	}

	@Override
	public void onItemSelected(Gab gab) {
		startActivity(BaseGabDetailActivity.getDetailIntent(this, gab));
	}

	@Override
	public void onResume() {
		super.onResume();
		gcmNotifications.startListening(1);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);    
	}

	@Override
	public void onLogout() {
		UserObserver.broadcastUserSwapped(null);
	}

	@Override
	protected void onStop() {
		super.onStop();
		gcmNotifications.stopListening();
	}
	
	@Override
	protected void onDestroy() 
	{
		UserObserver.unregisterObserver(userObserver);
		super.onDestroy();
	}

	@Override
	public void onNoPlayDialog(Dialog dialog) {
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				onLogout();		
			}});
		dialog.show();
	}

	@Override
	public void onNoPlay() {
		new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
		.setTitle(R.string.no_google_play_title)
		.setMessage(R.string.no_google_play_text)
		.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				onLogout();
			}
		})
		.show(); 	
	}

	@Override
	public void onUserChanged() {
		//don't care.
	}

	@Override
	public void onUserSwapped(User old, User newUser) {
		if(old != null) {
			final Intent ormUpdateIntent = new Intent(getApplicationContext(), ORMUpdateService.class); 
			getApplicationContext().stopService(ormUpdateIntent);
			OpenHelperManager.releaseHelper();
		}
		
		if(newUser != null) {			
			Database.setDatabaseForUser(newUser.getID());
			OpenHelperManager.getHelper(this, Database.class);
			final Intent ormUpdateIntent = new Intent(getApplicationContext(), ORMUpdateService.class);
			getApplicationContext().startService(ormUpdateIntent);
			
			GCM.getRegistrationID(newUser, this);
		}
		else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);    
			finish();
		}
	}

	@Override
	public void onNotification(String message, int gab_id) {
		//override and do nothing.
	}
}
