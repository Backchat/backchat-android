package com.youtell.backdoor.observers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

abstract public class ModelObserver<Observer> extends BroadcastReceiver {
	protected static LocalBroadcastManager broadcastManager;
		
    abstract public void onReceive(Context context, Intent intent);
	
	protected Observer observer;
	
	public ModelObserver(Observer observer)
	{
		this.observer = observer;		
	}

	protected abstract String[] getPossibleActions();
	
	private IntentFilter createIntentFilter() {
        IntentFilter filter = new IntentFilter();
        for(String action : getPossibleActions())
        	filter.addAction(action);
        
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        return filter;
	}
	
	public void startListening() {
		broadcastManager.registerReceiver(this, createIntentFilter());		
	}
	
	public void stopListening() {
		broadcastManager.unregisterReceiver(this);
	}
	
	static protected void broadcastChange(String someAction, Bundle args)
	{
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(someAction);
		broadcastIntent.putExtras(args);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastManager.sendBroadcast(broadcastIntent);
	}

	public static void initialize(Application application) {
		broadcastManager = LocalBroadcastManager.getInstance(application);
	}
}
