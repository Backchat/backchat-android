package com.youtell.backdoor;

import com.youtell.backdoor.observers.ModelObserver;
import com.youtell.backdoor.services.APIService;

public class Application extends android.app.Application
{
	@Override
	public void onCreate() {
		ModelObserver.initialize(this);
		APIService.initialize(this);
	}

}
