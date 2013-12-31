package com.youtell.backchat.observers;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.youtell.backchat.models.Gab;

public class GCMToastNotificationObserver extends GCMNotificationObserver {
	private int watching_gab_id;
	
	public void setWatchingGab(Gab g) {
		watching_gab_id = g.getID();
	}
	
	static public void toastWithMessage(Context context, String message) {
		Toast toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	public GCMToastNotificationObserver(final Context c) {
		super(null);
		
		this.context = c;
		observer = new GCMNotificationObserver.Observer() {
			@Override
			public void onNotification(String message, int gab_id) {
				GCMNotificationObserver.vibrateSoundNotify(c);
				
				if(watching_gab_id == GCMNotificationObserver.ALL_GABS || watching_gab_id != gab_id) {
					toastWithMessage(context, message);
				}
			}

			@Override
			public void onMixpanelMessage(String message) {
				//make a toast since we don't know where we are and maybe in a dialog
				toastWithMessage(context, message);
			}
			@Override
			public void onNewFriend(String message, int friend_id) {
				//make a toast since we don't know where we are and maybe in a dialog
				toastWithMessage(context, message);
			}
		};
		
	}
	
	public void startListening() {
		super.startListening(1);
	}

	public void disable() {
		observer = new GCMNotificationObserver.Observer() {

			@Override
			public void onNotification(String message, int gab_id) {				
			}

			@Override
			public void onMixpanelMessage(String message) {
				
			}

			@Override
			public void onNewFriend(String message, int friend_id) {
				// TODO Auto-generated method stub
				
			}
		};
	}

}
