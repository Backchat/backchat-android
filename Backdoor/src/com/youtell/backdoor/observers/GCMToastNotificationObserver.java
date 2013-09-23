package com.youtell.backdoor.observers;

import com.youtell.backdoor.models.Gab;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class GCMToastNotificationObserver extends GCMNotificationObserver {
	private int watching_gab_id;
	
	public void setWatchingGab(Gab g) {
		watching_gab_id = g.getID();
	}
	
	public GCMToastNotificationObserver(Context c) {
		super(null);
		
		this.context = c;
		observer = new GCMNotificationObserver.Observer() {
			@Override
			public void onNotification(String message, int gab_id) {
				if(watching_gab_id == GCMNotificationObserver.ALL_GABS || watching_gab_id != gab_id) {
					Toast toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
				}			
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
		};
	}

}
