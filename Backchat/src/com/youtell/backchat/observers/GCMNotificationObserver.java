package com.youtell.backchat.observers;

import com.youtell.backchat.models.Friend;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;

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
		public void onNewFriend(String message, int friend_id);
		public void onMixpanelMessage(String message);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(getResultCode() == Activity.RESULT_CANCELED)
			return;
		
		Bundle bundle = intent.getExtras();
		int type = bundle.getInt(ARG_TYPE);
		String message = bundle.getString(ARG_MESSAGE);

		if(type == TYPE_MESSAGE) {
			int this_gab_id = bundle.getInt(ARG_GAB_ID);
			if(gab_id == ALL_GABS || this_gab_id == gab_id) {
				observer.onNotification(message, this_gab_id);
			}
		}
		else if(type == TYPE_FRIENDNOTIF) {
			int friend_id = bundle.getInt(ARG_FRIEND_ID);
			observer.onNewFriend(message, friend_id);
		}
		else if(type == TYPE_MIXPANEL_MSG) {
			observer.onMixpanelMessage(message);
		}
		
		setResultCode(Activity.RESULT_CANCELED);
	}

	private static final String GCM_NOTIFICATION = "GCM_NOTIFICATION";
	private static final String ARG_GAB_ID = "ARG_GAB_ID";
	private static final String ARG_MESSAGE = "ARG_MESSAGE";
	private static final String ARG_TYPE = "ARG_TYPE";
	private static final String ARG_FRIEND_ID = "ARG_FRIEND_ID";
	
	private static int TYPE_MESSAGE = 1;
	private static int TYPE_FRIENDNOTIF = 2;
	private static int TYPE_MIXPANEL_MSG = 3;
	
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
	
	static public void broadcastMessage(Context context, String message, Gab g) {
		Bundle args = new Bundle();
		args.putInt(ARG_GAB_ID, g.getID());
		args.putString(ARG_MESSAGE, message);
		args.putInt(ARG_TYPE, TYPE_MESSAGE);
		Intent intent = new Intent();
		intent.putExtras(args);
		intent.setAction(GCM_NOTIFICATION);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		context.sendOrderedBroadcast(intent, null);
	}
	
	static public void broadcastMixpanelMessage(Context context, String message) {
		Bundle args = new Bundle();
		args.putString(ARG_MESSAGE, message);
		args.putInt(ARG_TYPE, TYPE_MIXPANEL_MSG);
		Intent intent = new Intent();
		intent.putExtras(args);
		intent.setAction(GCM_NOTIFICATION);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		context.sendOrderedBroadcast(intent, null);
	}
	
	static public void broadcastFriendNotification(Context context, String message, Friend f) {
		Bundle args = new Bundle();
		args.putInt(ARG_FRIEND_ID, f.getID());
		args.putString(ARG_MESSAGE, message);
		args.putInt(ARG_TYPE, TYPE_FRIENDNOTIF);
		Intent intent = new Intent();
		intent.putExtras(args);
		intent.setAction(GCM_NOTIFICATION);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		context.sendOrderedBroadcast(intent, null);
	}

	static public void vibrateSoundNotify(Context c) {
		if(User.getCurrentUser().getVibratePref(c)) {
			Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(100); //TODO set this value in prefs
		}
		
		if(User.getCurrentUser().getSoundPref(c)) {
			Uri uri = Settings.System.DEFAULT_NOTIFICATION_URI;
			RingtoneManager.getRingtone(c, uri).play();
		}
	}
}
