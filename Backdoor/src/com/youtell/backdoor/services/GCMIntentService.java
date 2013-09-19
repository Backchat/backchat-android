package com.youtell.backdoor.services;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youtell.backdoor.gcm.BroadcastReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GCMIntentService extends IntentService {
	private static int GCM_KIND_MSG = 0;
	private static int GCM_KIND_FRIEND = 1;
	
	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
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
				int kind = extras.getInt("kind");
				

				//TODO
				if(kind == GCM_KIND_MSG) {
					int gab_id = extras.getInt("gab_id");
					int unread_count = extras.getInt("unread_count");
					Log.i("GCMIntent", String.format("%s %d %d %d", message, gab_id, unread_count, kind));
				}
				else if(kind == GCM_KIND_FRIEND) {
					
				}
			}
		}

		BroadcastReceiver.completeWakefulIntent(intent);
	}

}
