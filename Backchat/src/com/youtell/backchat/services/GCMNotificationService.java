package com.youtell.backchat.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.youtell.backchat.R;
import com.youtell.backchat.activities.BaseGabDetailActivity;
import com.youtell.backchat.activities.GabListActivity;
import com.youtell.backchat.activities.StartupActivity;
import com.youtell.backchat.models.Friend;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.GCMNotificationObserver;

public class GCMNotificationService extends Service implements GCMNotificationObserver.Observer {
	private GCMNotificationObserver observer;

	@Override
	public void onCreate() {
		super.onCreate();
		observer = new GCMNotificationObserver(this, null);
		observer.startListening(0);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		observer.stopListening();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onNotification(String message, int gab_id) {
		Log.e("GCMIntentService", "creating notification");

		Gab g = Gab.getByID(gab_id);
		Intent gabDetail = BaseGabDetailActivity.getDetailIntent(this, g);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(BaseGabDetailActivity.getDetailIntentClass(g));
		stackBuilder.addNextIntent(gabDetail);
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
		fireMainAppNotification(message);
	}
	
	private void fireMainAppNotification(String message) {
		Intent openMainApp = new Intent(this, GabListActivity.class); //you are guarenteed to be logged in to receive anythign.
		openMainApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

	@Override
	public void onNewFriend(String message, int friend_id) {
		//TODO better message?
		//TODO make the click go to a new message, nto main screen
		String full_message = message;
		Friend f = Friend.getByID(friend_id);
		if(f == null) {
			//maybe dropped, deleted,...
			return;
		}
		
		fireMainAppNotification(full_message);
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
		return Service.START_STICKY;	
	}	

}
