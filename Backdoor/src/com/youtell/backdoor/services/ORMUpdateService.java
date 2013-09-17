package com.youtell.backdoor.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.youtell.backdoor.observers.MessageListObserver;

public class ORMUpdateService extends Service {
	MessageListObserver messageObserver;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("ORMUpdate", "started");
		messageObserver = new MessageListObserver(new MessageListObserver.Observer() {
			@Override
			public void onChange(String action, int gabID, int messageID) {
				if(action == MessageListObserver.MESSAGE_ADDED) {
					
				}				
			}
		
		}, null);
		
		messageObserver.startListening();
		return Service.START_STICKY;	
	}
	
	@Override
    public void onDestroy() {
		Log.v("ORMUpdate", "stopped");
		messageObserver.stopListening();
		super.onDestroy();		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
