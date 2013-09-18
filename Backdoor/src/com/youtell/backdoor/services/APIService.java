package com.youtell.backdoor.services;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.params.HttpParams;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.otto.Subscribe;
import com.youtell.backdoor.Util;
import com.youtell.backdoor.api.Request;
import com.youtell.backdoor.models.Database;
import com.youtell.backdoor.models.ModelBus;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.models.DBClosedEvent;

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
	
	public static void initialize(Context c) {
		applicationContext = c.getApplicationContext();
		userAgentString = String.format("ANDROID %s", Util.getVersionName(applicationContext));
		final Intent ormUpdateIntent = new Intent(applicationContext, ORMUpdateService.class); //TODO?

		ModelBus.events.register(new Object() {
			@Subscribe public void DBAvailable(Database db)
			{
				applicationContext.startService(ormUpdateIntent);
			}

			@Subscribe public void DBClosed(DBClosedEvent e)
			{
				applicationContext.stopService(ormUpdateIntent);
			}
		});		
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		OpenHelperManager.releaseHelper();
		client.close();
	}	

	@Override
	public void onCreate()
	{
		super.onCreate();
		OpenHelperManager.getHelper(this, Database.class);
		client = AndroidHttpClient.newInstance(userAgentString);
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
		User user = new User();//TODO
		r.execute(client, user);		
	}
}
