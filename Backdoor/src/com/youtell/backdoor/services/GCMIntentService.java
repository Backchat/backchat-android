package com.youtell.backdoor.services;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youtell.backdoor.R;
import com.youtell.backdoor.activities.BaseGabDetailActivity;
import com.youtell.backdoor.gcm.BroadcastReceiver;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.GCMNotificationObserver;
import com.youtell.backdoor.observers.LocalObserver;
import com.youtell.backdoor.observers.UserObserver;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class GCMIntentService extends IntentService implements GCMNotificationObserver.Observer {
	private static int GCM_KIND_MSG = 0;
	private static int GCM_KIND_FRIEND = 1;
	private GCMNotificationObserver observer;

	public GCMIntentService() {
		super("GCMIntentService");
		
		observer = new GCMNotificationObserver(this, null);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(User.getCurrentUser() == null) //TODO check ID?
			return;
		
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			/*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore
			 * any message types you're not interested in, or that you don't
			 * recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {				
				String message = extras.getString("message");
				int kind = Integer.parseInt(extras.getString("kind"));

				if(kind == GCM_KIND_MSG) {
					int gab_id = Integer.parseInt(extras.getString("gab_id"));
					int unread_count = Integer.parseInt(extras.getString("unread_count"));
					
					Gab g = Gab.getByRemoteID(gab_id);
					
					if(g == null) {
						g = new Gab();
						g.setRemoteID(gab_id);
						g.save();
					}
					
					g.updateWithMessages();
					
					GCMNotificationObserver.broadcastNotification(this, message, g);
				}
				else if(kind == GCM_KIND_FRIEND) {

				}
			}
		}

		BroadcastReceiver.completeWakefulIntent(intent);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		observer.startListening(0);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		observer.stopListening();
	}

	@Override
	public void onNotification(String message, int gab_id) {
		Log.e("GCMIntentService", "creating notification");

		Gab g = Gab.getByID(gab_id);
		Intent gabDetail = BaseGabDetailActivity.getDetailIntent(this, g);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addNextIntentWithParentStack(gabDetail);
		// Gets a PendingIntent containing the entire back stack
		PendingIntent pendingIntent =
		        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification note = new NotificationCompat.Builder(this)
		.setContentTitle("Backdoor") //TODO stringify
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentText(message)
		.setTicker(message)
		.setContentIntent(pendingIntent)
		.setAutoCancel(true)
		.build();
		//						TODO
		int mNotificationId = 001;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, note);
	}
}
