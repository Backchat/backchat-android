package com.youtell.backdoor.services;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.youtell.backdoor.Application;
import com.youtell.backdoor.Util;
import com.youtell.backdoor.api.PostLoginRequest;
import com.youtell.backdoor.api.Request;
import com.youtell.backdoor.models.Database;
import com.youtell.backdoor.models.User;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;

public class APIService extends IntentService {
	public static Context applicationContext = null; //TODO
	private static String userAgentString;
	private AndroidHttpClient client;
	public static MixpanelAPI mixpanel;
	
	public static void initialize(Context c) {
		applicationContext = c.getApplicationContext();
		userAgentString = String.format("ANDROID %s", Util.getVersionName(applicationContext));		
	}

	@Override
	public void onDestroy() 
	{
		if(mixpanel != null)
			mixpanel.flush();
		
		OpenHelperManager.releaseHelper();
		client.close();
		
		super.onDestroy();
	}	

	@Override
	public void onCreate()
	{
		super.onCreate();
		client = AndroidHttpClient.newInstance(userAgentString);
	}

	public APIService() {
		super("APIService");
	}

	private static final String ARGS = "ARGS";
	private static final String USER_ARG = "USER_ARG";
	
	public static void fire(Request r)
	{
		Bundle args = r.getArguments();
		Intent fireIntent = new Intent(applicationContext, APIService.class);
		fireIntent.putExtra(ARGS, args);
		Bundle userBundle = new Bundle();
		if(User.getCurrentUser() != null) {
			User.getCurrentUser().serialize(userBundle);
			fireIntent.putExtra(USER_ARG, userBundle);
		}
		applicationContext.startService(fireIntent);
	}

	private int lastUserID = -1;
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Request r = Request.inflateRequest(intent.getBundleExtra(ARGS));
		r.setContext(applicationContext);
		User user = null;
		
		if(intent.hasExtra(USER_ARG)) {
			user = new User();
			user.deserialize(intent.getBundleExtra(USER_ARG));
		}
		
		if(user != null && user.getID() != lastUserID) {
			if(lastUserID != -1)
				OpenHelperManager.releaseHelper();
			
			lastUserID = user.getID();
			Database.setDatabaseForUser(user.getID());
			OpenHelperManager.getHelper(this, Database.class);
			
			if(mixpanel != null) {
				mixpanel.flush();
			}
			
			mixpanel = Application.getMixpanelInstance(applicationContext);
			Log.e("MIXPANEL", String.format("identify %d", user.getID()));
			mixpanel.identify(String.format("%d", user.getID()));
		}
		
		int userID = -1;
		if(user != null) {
			userID = user.getID();
		}
		
		Log.v("APIService", String.format("intent %s, user %d", r.getClass().getName(), userID));
		
		if(r instanceof PostLoginRequest) {//URGH TODO
			r.execute(client, null);
		}
		else {
			r.execute(client, user);
		}	
	}
}
