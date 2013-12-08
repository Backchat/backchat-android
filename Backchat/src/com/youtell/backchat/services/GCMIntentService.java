package com.youtell.backchat.services;

import java.util.Date;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youtell.backchat.activities.BaseGabDetailActivity;
import com.youtell.backchat.activities.GabListActivity;
import com.youtell.backchat.gcm.BroadcastReceiver;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.GCMNotificationObserver;
import com.youtell.backchat.observers.LocalObserver;
import com.youtell.backchat.observers.UserObserver;
import com.youtell.backchat.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
				if(extras.containsKey("mp_message")) {
					//mixpanel message
			        String mp_message = extras.getString("mp_message");
			        if(mp_message != null && mp_message.length() > 0)
			        {
			        	GCMNotificationObserver.broadcastMixpanelMessage(this, mp_message);
			        }
				}
				else {
					String message = extras.getString("message");
					int kind = Integer.parseInt(extras.getString("kind"));

					if(kind == GCM_KIND_MSG) {
						int gab_id = Integer.parseInt(extras.getString("gab_id"));
						int unread_count = Integer.parseInt(extras.getString("unread_count"));

						Gab g = Gab.getByRemoteID(gab_id);

						if(g == null) {
							g = new Gab();
							g.setRemoteID(gab_id);
							g.setUpdatedAt(new Date());
							g.save();
						}

						g.updateWithMessages();

						GCMNotificationObserver.broadcastMessage(this, message, g);
					}
					else if(kind == GCM_KIND_FRIEND) {

					}
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
		
		NotificationCompat.Builder notebuilder = new NotificationCompat.Builder(this)
		.setContentTitle(getResources().getText(R.string.app_name))
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentText(message)
		.setTicker(message)
		.setContentIntent(pendingIntent)
		.setAutoCancel(true);
		
		fireNotification(notebuilder);
	}

	@Override
	public void onMixpanelMessage(String message) {
		Intent openMainApp = new Intent(this, GabListActivity.class);
		openMainApp.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //TODO merge this
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openMainApp, PendingIntent.FLAG_ONE_SHOT);
		NotificationCompat.Builder notebuilder = new NotificationCompat.Builder(this)
		.setContentTitle(getResources().getText(R.string.app_name))
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentText(message)
		.setTicker(message)
		.setContentIntent(pendingIntent)
		.setAutoCancel(true);
		fireNotification(notebuilder);
	}
		
	
	private void fireNotification(NotificationCompat.Builder notebuilder) {
		if(User.getCurrentUser().getVibratePref(this))
			notebuilder.setVibrate(new long[] {0, 100});
		
		if(User.getCurrentUser().getSoundPref(this))
			notebuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

		Notification note = notebuilder.build();
		//						TODO
		int mNotificationId = 001;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, note);		
	}
}
