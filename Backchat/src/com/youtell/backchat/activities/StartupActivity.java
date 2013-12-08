package com.youtell.backchat.activities;

import android.content.Intent;
import android.os.Bundle;

import com.youtell.backchat.Application;
import com.youtell.backchat.gcm.GCM;
import com.youtell.backchat.models.User;
import com.youtell.backchat.social.SocialProvider;

public class StartupActivity extends android.app.Activity implements SocialProvider.Callback {
	private User cachedUser = null;
	private SocialProvider socialProvider = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//destroyed and flushed in gablistactivity
		Application.mixpanel = Application.getMixpanelInstance(getApplicationContext());

		cachedUser = User.getCachedUser(this);

		if(cachedUser != null) {
			cachedUser.setGCMKey(GCM.GCM_KEY); //TODO dynamic merge with postloginrequest
			String providerName = User.getCachedSocialProvider(this);
			socialProvider = SocialProvider.createByProviderName(providerName, this);
			socialProvider.tryCachedLogin(this);
		}
		else {
			startLogin();
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
		User.setCurrentUser(cachedUser);
		SocialProvider.setActiveProvider(socialProvider);		
		Intent intent = new Intent(this, GabListActivity.class);
		intent.putExtra(GabListActivity.START_ARG, GabListActivity.STARTUP_START);
		startIntent(intent);
	}

	@Override
	public void onFailedLogin() {
		startLogin();
	}
}
