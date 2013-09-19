package com.youtell.backdoor.services;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.youtell.backdoor.Util;
import com.youtell.backdoor.api.Request;
import com.youtell.backdoor.models.Database;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.UserObserver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;

public class APIService extends IntentService implements UserObserver.Observer {
	public static Context applicationContext = null; //TODO
	private static String userAgentString;
	private AndroidHttpClient client;
	private Object userObserver;

	public static void initialize(Context c) {
		applicationContext = c.getApplicationContext();
		userAgentString = String.format("ANDROID %s", Util.getVersionName(applicationContext));		
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		UserObserver.unregisterObserver(userObserver);
		OpenHelperManager.releaseHelper();
		client.close();
	}	

	@Override
	public void onCreate()
	{
		super.onCreate();
		client = AndroidHttpClient.newInstance(userAgentString);
		userObserver = UserObserver.registerObserver(this);
	}

	public APIService() {
		super("APIService");
	}

	public static void fire(Request r)
	{
		Bundle args = r.getArguments();
		Intent fireIntent = new Intent(applicationContext, APIService.class);
		fireIntent.putExtras(args);
		applicationContext.startService(fireIntent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {		
		Request r = Request.inflateRequest(intent);
		Log.v("APIService", String.format("intent %s", r.getClass().getName()));
		if(user != null) {
			r.execute(client, user.clone());
		}
		else {
			Log.e("APIService", "attempted to handle an intent but user was null");
		}
	}

	private User user;

	@Override
	public void onUserChanged() {	
	}

	@Override
	public void onUserSwapped(User old, User newUser) {
		if(old != null) {
			OpenHelperManager.releaseHelper();
		}
		
		if(newUser != null) {
			Database.setDatabaseForUser(newUser.getID());
			OpenHelperManager.getHelper(this, Database.class);
		}

		user = newUser;
	}
}
