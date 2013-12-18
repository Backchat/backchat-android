package com.youtell.backchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.LocalObserver;
import com.youtell.backchat.services.APIService;

import net.hockeyapp.android.CrashManager;

public class Application extends android.app.Application
{	
	@Override
	public void onCreate() {
		LocalObserver.initialize(this);
		APIService.initialize(this);

		//TODO enable correct caching...
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);
	}
	
	public static MixpanelAPI mixpanel;

	public static MixpanelAPI getMixpanelInstance(Context context) 
	{
	    MixpanelAPI instance = MixpanelAPI.getInstance(context, Settings.settings.mixpanelToken);
	    instance.logPosts();
	    return instance;
	}
	
	public static void identifyUserToMixpanel(MixpanelAPI api, User user) {
		String distinctId = String.format("%d", user.getID());
		Log.e("MIXPANEL", String.format("identified %s", distinctId));
		api.identify(distinctId);
		api.getPeople().identify(distinctId);
	}

	public static void checkCrashLog(Context context) {
		CrashManager.register(context, Settings.settings.hockeyToken);
	}
}
