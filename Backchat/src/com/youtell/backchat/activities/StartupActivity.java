package com.youtell.backchat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.youtell.backchat.Application;
import com.youtell.backchat.gcm.GCM;
import com.youtell.backchat.models.Database;
import com.youtell.backchat.models.User;
import com.youtell.backchat.services.GCMNotificationService;
import com.youtell.backchat.services.ORMUpdateService;
import com.youtell.backchat.social.SocialProvider;

public class StartupActivity extends android.app.Activity implements SocialProvider.Callback {
	private User cachedUser = null;
	private SocialProvider socialProvider = null;
	
	static public void loginUser(User user, SocialProvider provider, Context applicationContext) {
		User.setCurrentUser(user);
		SocialProvider.setActiveProvider(provider);
		Application.identifyUserToMixpanel(Application.mixpanel, user);
		Log.e("DB", String.format("%d", user.getID()));
		Database.setDatabaseForUser(user.getID());
		OpenHelperManager.getHelper(applicationContext, Database.class);
		
		final Intent ormUpdateIntent = new Intent(applicationContext, ORMUpdateService.class);
		applicationContext.startService(ormUpdateIntent);
		final Intent notificationIntent = new Intent(applicationContext, GCMNotificationService.class);
		applicationContext.startService(notificationIntent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(User.getCurrentUser() != null) {
			//we already have logged in and stuff, just go straight to
			Log.e("gablistactivity", "startup->already logged in");
			goToMainScreen(GabListActivity.RESUME_FROM_START);
		}
		else {
			Log.e("gablistactivity", "startupcreate");

			Application.mixpanel = Application.getMixpanelInstance(getApplicationContext());

			cachedUser = User.getCachedUser(this);

			if(cachedUser != null) {
				String providerName = User.getCachedSocialProvider(this);
				socialProvider = SocialProvider.createByProviderName(providerName, this);
				socialProvider.tryCachedLogin(this);
				Log.e("gablistactivity", "cached login");
			}
			else {
				Log.e("gablistactivity", "->login screen");

				startLogin();
			}
		}
	}

	private void startLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(LoginActivity.FIRST_START_ARG, true);
		startIntent(intent);
	}
	
	private void startIntent(Intent intent) {
		startActivity(intent);
		overridePendingTransition(0,0);	
		finish();
	}
	
	@Override
	public void onAuthenticated(SocialProvider p) {
		loginUser(cachedUser, socialProvider, getApplicationContext());
		goToMainScreen(GabListActivity.STARTUP_START);
	}

	private void goToMainScreen(int how) {
		Intent intent = new Intent(this, GabListActivity.class);
		intent.putExtra(GabListActivity.START_ARG, how);
		startIntent(intent);
	}
	
	@Override
	public void onFailedLogin() {
		startLogin();
	}
}
