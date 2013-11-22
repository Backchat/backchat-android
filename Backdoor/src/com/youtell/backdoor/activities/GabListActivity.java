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
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.squareup.otto.Subscribe;
import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostAbuseReportRequest;
import com.youtell.backdoor.fragments.GabCluesFragment;
import com.youtell.backdoor.fragments.GabListFragment;
import com.youtell.backdoor.fragments.NotificationSettingsFragment;
import com.youtell.backdoor.fragments.SettingsMenuFragment;
import com.youtell.backdoor.fragments.WebViewDialogFragment;
import com.youtell.backdoor.gcm.GCM;
import com.youtell.backdoor.iap.BuyClueIAP;
import com.youtell.backdoor.models.DBClosedEvent;
import com.youtell.backdoor.models.Database;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.GCMNotificationObserver;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.observers.UserObserver.Observer;
import com.youtell.backdoor.services.APIService;
import com.youtell.backdoor.services.ORMUpdateService;
import com.youtell.backdoor.social.SocialProvider;

public class GabListActivity extends SlidingActivity implements GabListFragment.Callbacks, SettingsMenuFragment.Callbacks, GCM.Callbacks,
GCMNotificationObserver.Observer, SocialProvider.ShareCallback, BuyClueIAP.Observer {
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private GCMNotificationObserver gcmNotifications;
	private BuyClueIAP buyClue = new BuyClueIAP(this);
	private SocialProvider.ShareHelper shareHelper;
	private int startFrom;
	
	public static final int LOGIN_START = 1;
	public static final int STARTUP_START = 2;
	private static final int RESUME_START = 3;
	public static final String START_ARG = "START_ARG";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		User user = User.getCurrentUser();
		Log.e("DB", String.format("%d", user.getID()));
		Database.setDatabaseForUser(user.getID());
		OpenHelperManager.getHelper(this, Database.class);
		final Intent ormUpdateIntent = new Intent(getApplicationContext(), ORMUpdateService.class);
		getApplicationContext().startService(ormUpdateIntent);

		GCM.getRegistrationID(user, this);

		shareHelper = SocialProvider.getActiveProvider().getShareHelper(this);
		shareHelper.onCreate(savedInstanceState);	
		
		boolean showTour = false;
		Bundle extras = getIntent().getExtras();
		startFrom = RESUME_START;
		if(extras != null) {
			startFrom = extras.getInt(START_ARG);
		}

		showTour = user.isNewUser();
		
		if(showTour) {
			Intent intent = new Intent(this, TourActivity.class);
			startActivity(intent);
		}
		else {
			BaseActivity.runOnNextScreen(this, new Runnable() {

				@Override
				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(), 
							getResources().getText(R.string.login_success), Toast.LENGTH_SHORT); //TODO add name

					toast.show();					
				}
				
			});
		}
				
		new Thread(new Runnable() {
			@Override
			public void run() {
				SocialProvider.getActiveProvider().getUserInfo();
			}
			
		}).start();
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
	public void onResume() {
		super.onResume();
		gcmNotifications.startListening(1);
		shareHelper.onResume();
		if(startFrom == LOGIN_START) {
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
			startFrom = RESUME_START;
		}
		else if(startFrom == STARTUP_START) {
			startFrom = RESUME_START;
		}
		else {
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		}
		
    	buyClue.connect();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);    
	}

	@Override
	public void onLogout() {
		User.setCurrentUser(null);
		OpenHelperManager.releaseHelper();	

		final Intent ormUpdateIntent = new Intent(getApplicationContext(), ORMUpdateService.class); 
		getApplicationContext().stopService(ormUpdateIntent);	

		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);    
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();

		gcmNotifications.stopListening();
	}

	@Override
	protected void onDestroy() 
	{
		shareHelper.onDestroy();
		buyClue.disconnect();		
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
	public void onNotification(String message, int gab_id) {
		//override and do nothing.
	}

	@Override
	public void onGabSelected(Gab gab) {
		startActivity(BaseGabDetailActivity.getDetailIntent(this, gab));		
	}

	@Override
	public void onFriendSelected(Friend f) {
		//TODO stop the listeners so we don't refresh the gab before going across
		Gab gab = f.createNewGab();
		gab.save();
		startActivity(BaseGabDetailActivity.getDetailIntent(this, gab));	
	}

	@Override
	public void onBuyClue() {
		buyClue.present(User.getCurrentUser().clone());
	}

	@Override
	public void onInvite() {
		inviteClick(null);
	}

	@Override
	public void onShareApp() {
		shareHelper.shareApp();
	}

	@Override
	public void onPause() {
		super.onPause();
		shareHelper.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		shareHelper.onSaveInstanceState(state);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		shareHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onMoreFriends() {
		newGabClick(null);
	}

	@Override
	public void onChangeNotificationSettings() {
		NotificationSettingsFragment notifications = new NotificationSettingsFragment();
		notifications.show(getFragmentManager(), "notifications");
	}

	@Override
	public void onReportAbuse() {
		final EditText abuseInfo = new EditText(this);

		abuseInfo.setHint(R.string.abuse_dialog_info_hint);
		abuseInfo.setMinLines(3);
		abuseInfo.setMaxLines(5);
		abuseInfo.setBackgroundColor(getResources().getColor(R.color.light_grey_background));
		abuseInfo.setGravity(Gravity.TOP);

		new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
		.setTitle(R.string.abuse_dialog_title)
		.setMessage(R.string.abuse_dialog_text)
		.setView(abuseInfo)
		.setPositiveButton(R.string.abuse_dialg_report_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				APIService.fire(new PostAbuseReportRequest(abuseInfo.getText().toString()));
			}
		})
		.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		})
		.show(); 
	}

	@Override
	public void onAboutUs() {
		webViewDialog("http://backdoorapp.com/about", R.string.about_us_dialog_title);
	}

	@Override
	public void onPrivacyLegal() {
		webViewDialog("file:///android_asset/privacy.html", R.string.privacy_dialog_title);
	}

	private void webViewDialog(String uri, int titleRes) {
		WebViewDialogFragment dialog = new WebViewDialogFragment();
		Bundle args = new Bundle();
		args.putString(WebViewDialogFragment.URI_ARGUMENT, uri);
		args.putInt(WebViewDialogFragment.TITLE_RES_ARGUMENT, titleRes);
		dialog.setArguments(args);
		dialog.show(getFragmentManager(), "webView");
	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReadyIAP() {
		//TODO
	}
}
