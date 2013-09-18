package com.youtell.backdoor;

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

}
