package com.youtell.backdoor;

import android.app.Activity;
import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
}
