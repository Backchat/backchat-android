package com.youtell.backchat.observers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public abstract class BaseLocalObserver<ObserverType> extends BroadcastReceiver {
	protected static LocalBroadcastManager broadcastManager;
    abstract public void onReceive(Context context, Intent intent);
	
	protected ObserverType observer;

	public BaseLocalObserver(ObserverType observer)
	{
		this.observer = observer;		
	}
	
	protected abstract String[] getPossibleActions();

	private IntentFilter createIntentFilter(int priority) {
        IntentFilter filter = new IntentFilter();
        for(String action : getPossibleActions())
        	filter.addAction(action);
        
        filter.setPriority(priority);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        return filter;
	}
	
	public void startListening() {
		broadcastManager.registerReceiver(this, createIntentFilter(0));
	}
	
	public void startListening(int priority) {
		broadcastManager.registerReceiver(this, createIntentFilter(priority));		
	}
	
	public void stopListening() {
		broadcastManager.unregisterReceiver(this);
	}
	
	static protected Intent broadcastChange(String someAction, Bundle args)
	{
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(someAction);
		broadcastIntent.putExtras(args);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastManager.sendBroadcast(broadcastIntent);
		return broadcastIntent;
	}

	public static void initialize(Application application) {
		broadcastManager = LocalBroadcastManager.getInstance(application);
	}
}
