package com.youtell.backdoor.observers;

import com.youtell.backdoor.models.Gab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class GCMNotificationObserver extends BroadcastReceiver {
	protected int gab_id;
	protected static final int ALL_GABS = -1;
	protected Observer observer;
	protected Context context;
	private boolean registered;
	
	protected GCMNotificationObserver(Gab g) {	
		gab_id = ALL_GABS;
		if(g != null) {
			gab_id = g.getID();
		}
		
		registered = false;
	}

	public <T extends Context & Observer> GCMNotificationObserver(T observer, Gab g) {
		this.observer = observer;
		this.context = observer;
		gab_id = ALL_GABS;
		if(g != null) {
			gab_id = g.getID();
		}
	}

	public interface Observer {
		public void onNotification(String message, int gab_id);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(getResultCode() == Activity.RESULT_CANCELED)
			return;
		
		Bundle bundle = intent.getExtras();
		int this_gab_id = bundle.getInt(ARG_GAB_ID);
		String message = bundle.getString(ARG_MESSAGE);
		if(gab_id == ALL_GABS || this_gab_id == gab_id) {
			observer.onNotification(message, this_gab_id);

			setResultCode(Activity.RESULT_CANCELED);
		}
		
	}

	private static final String GCM_NOTIFICATION = "GCM_NOTIFICATION";
	private static final String ARG_GAB_ID = "ARG_GAB_ID";
	private static final String ARG_MESSAGE = "ARG_MESSAGE";
	
	public void startListening(int priority) {
		IntentFilter filter = new IntentFilter();
        filter.setPriority(priority);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(GCM_NOTIFICATION);
        
		context.registerReceiver(this, filter);
		registered = true;
	}

	public void stopListening() {
		if(registered) {
			context.unregisterReceiver(this);
			registered = false;
		}
	}
	
	static public void broadcastNotification(Context context, String message, Gab g) {
		Bundle args = new Bundle();
		args.putInt(ARG_GAB_ID, g.getID());
		args.putString(ARG_MESSAGE, message);
		//broadcastChange(GCM_NOTIFICATION, args);
		Intent intent = new Intent();
		intent.putExtras(args);
		intent.setAction(GCM_NOTIFICATION);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		context.sendOrderedBroadcast(intent, null);
	}

}
