package com.youtell.backchat.services;

import java.util.Date;

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

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youtell.backchat.R;
import com.youtell.backchat.activities.BaseGabDetailActivity;
import com.youtell.backchat.activities.GabListActivity;
import com.youtell.backchat.api.GetFriendsForNotificationRequest;
import com.youtell.backchat.api.GetFriendsRequest;
import com.youtell.backchat.gcm.BroadcastReceiver;
import com.youtell.backchat.models.Friend;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.GCMNotificationObserver;

public class GCMIntentService extends IntentService {
	private static int GCM_KIND_MSG = 0;
	private static int GCM_KIND_FRIEND = 1;
		
	public GCMIntentService() {
		super("GCMIntentService");
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
					int unread_count = Integer.parseInt(extras.getString("unread_count"));

					if(kind == GCM_KIND_MSG) {
						int gab_id = Integer.parseInt(extras.getString("gab_id"));

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
						int friend_id = Integer.parseInt(extras.getString("friendship_id"));
						Friend f = Friend.getByRemoteID(friend_id);
						if(f == null) {
							APIService.fire(new GetFriendsForNotificationRequest(message, friend_id));
						}
						else {
							GCMNotificationObserver.broadcastFriendNotification(this, message, f);
						}						
					}
				}
			}
		}

		BroadcastReceiver.completeWakefulIntent(intent);
	}
}
