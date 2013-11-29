package com.youtell.backdoor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.LocalObserver;
import com.youtell.backdoor.services.APIService;

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
	    MixpanelAPI instance = MixpanelAPI.getInstance(context, "b773675a02695460a27af2b8c2d11d39"); //TODO dynamic-ify
	    instance.logPosts();
	    return instance;
	}
	
	public static void identifyUserToMixpanel(MixpanelAPI api, User user) {
		String distinctId = String.format("%d", user.getID());
		Log.e("MIXPANEL", String.format("identified %s", distinctId));
		api.identify(distinctId);
		api.getPeople().identify(distinctId);
	}
}
