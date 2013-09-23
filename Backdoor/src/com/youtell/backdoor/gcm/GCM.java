package com.youtell.backdoor.gcm;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youtell.backdoor.models.User;

public class GCM {
	public interface Callbacks {
		public void onNoPlayDialog(Dialog dialog);
		public void onNoPlay();
	}
	
	private static final String DEVICE_REGISTRATION_ID_PROP = "DEVICE_REGISTRATION_ID";
	private static final String VERSION_INT_PROP = "VERSION_INT_PROP";
	private static final String PREFS_GCM_SETTINGS = "PREFS_GCM_SETTINGS";
	public static final String GCM_KEY = "412155847073"; //TODO dynamic		

	public static void getRegistrationID(final User user, final Activity activity) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

				final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

				if (resultCode != ConnectionResult.SUCCESS) {
					final boolean recoverable = GooglePlayServicesUtil.isUserRecoverableError(resultCode);								
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(recoverable) {
								Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
										PLAY_SERVICES_RESOLUTION_REQUEST);
								if(dialog != null) {
									((Callbacks)activity).onNoPlayDialog(dialog);
									return;
								}
							} 

							Log.i("GCM", "This device is not supported.");
							((Callbacks)activity).onNoPlay();
						}
					});
				}
				else {
					//yes, we're on.					
					SharedPreferences prefs = activity.getSharedPreferences(
							String.format("%s_%s", PREFS_GCM_SETTINGS, user.getGCMKey()),
							Context.MODE_PRIVATE);
					
					String deviceRegistrationID = prefs.getString(DEVICE_REGISTRATION_ID_PROP, "");
					int storedVersion = prefs.getInt(VERSION_INT_PROP, -1);
					
					int currentVersion;
					try {
						currentVersion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						currentVersion = storedVersion; //just assume we're not updating.
					}
					
				    if (storedVersion != currentVersion) {
				    	deviceRegistrationID = "";
				    }
				    
				    if(deviceRegistrationID.isEmpty()) {
				    	Log.i("GCM", "Actually getting a new deviceRegistration");
				    	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
				    	try {
							deviceRegistrationID = gcm.register(user.getGCMKey());
					    	
					    	Editor edit = prefs.edit();
					    	edit.putString(DEVICE_REGISTRATION_ID_PROP, deviceRegistrationID);
					    	edit.putInt(VERSION_INT_PROP, currentVersion);
					    	edit.commit();					    	
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
				    }
				    
					Log.e("GCM", String.format("GCM ID: %s", deviceRegistrationID));

				    user.setDeviceID(deviceRegistrationID);
				}
			}
		});
		
		t.start();
	}
}
