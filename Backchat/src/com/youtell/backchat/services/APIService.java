package com.youtell.backchat.services;


import java.util.concurrent.atomic.AtomicInteger;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.youtell.backchat.Application;
import com.youtell.backchat.Util;
import com.youtell.backchat.api.PostLoginRequest;
import com.youtell.backchat.api.Request;
import com.youtell.backchat.models.User;

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
	private static AtomicInteger requestID = new AtomicInteger(0);
	
	private static int getNewRequestID() {
		int id = requestID.getAndIncrement();
		return id;
	}
	
	public static void fire(Request r)
	{
		r.setRequestID(getNewRequestID());
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
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Request r = Request.inflateRequest(intent.getBundleExtra(ARGS));
		r.setContext(this);
		
		User user = null;
		
		if(intent.hasExtra(USER_ARG)) {
			user = new User();
			user.deserialize(intent.getBundleExtra(USER_ARG));
		}
		
		if(user != null) {
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
			if(User.getCurrentUser() == null || User.getCurrentUser().getID() != userID)
				return;						

			r.execute(client, user);
		}	
	}
}
