package com.youtell.backdoor.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.youtell.backdoor.api.GetFeaturedRequest;
import com.youtell.backdoor.api.GetFriendsRequest;
import com.youtell.backdoor.api.GetGabsRequest;
import com.youtell.backdoor.api.PostDeviceRequest;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.services.APIService;
import com.youtell.backdoor.social.SocialProvider;

public class User implements InflatableObject {

	public static final int UNKNOWN_CLUE_COUNT = -1;
	
	public void updateGabs() {
		APIService.fire(new GetGabsRequest());
	}

	private String apiToken;
	
	public void setApiToken(String s) {
		apiToken = s;
	}
	
	public String getApiToken() {
		return apiToken;
	}

	public void getFriends() {
		APIService.fire(new GetFriendsRequest());
		APIService.fire(new GetFeaturedRequest());
	}

	private String hostName;
	
	public void setApiServerHostName(String host) {
		hostName = host;
	}
	
	public String getApiServerHostName() {
		return hostName; 
	}

	private String fullName;
	
	public void setFullName(String s) {
		fullName = s;
	}
	
	public String getFullName() {
		return fullName;
	}

	private int totalClueCount = UNKNOWN_CLUE_COUNT;
	
	public void setTotalClueCount(int n) {
		totalClueCount = n;
	}
	public int getTotalClueCount() {
		return totalClueCount; 
	}

	private String GCMKey;
	public void setGCMKey(String key) {
		GCMKey = key;
	}
	
	public String getGCMKey() {
		return GCMKey;
	}
	
	public void setDeviceID(String id) {
		APIService.fire(new PostDeviceRequest(id));
	}

	private static final String TAG = "USER";
	
	public void deserialize(Bundle b) {		
		setGCMKey(b.getString("GCMKey"));
		this.totalClueCount = b.getInt("totalClueCount");
		setApiServerHostName(b.getString("hostName"));
		setApiToken(b.getString("apiToken"));
		setFullName(b.getString("fullName"));
		this.message_preview = b.getBoolean("message_preview");
		setID(b.getInt("id"));
	}
	
	public void serialize(Bundle b) {
		Log.i(TAG, "serialized");
		b.putString("GCMKey", getGCMKey());
		b.putInt("totalClueCount", getTotalClueCount());
		b.putString("hostName", getApiServerHostName());
		b.putString("apiToken", getApiToken());
		b.putString("fullName", getFullName());
		b.putBoolean("message_preview", message_preview);
		b.putInt("id", id);
	}
	
	public User clone() {
		User u = new User();
		u.apiToken = apiToken;
		u.fullName = fullName;
		u.GCMKey = GCMKey;
		u.hostName = hostName;
		u.id = id;
		u.totalClueCount = totalClueCount;
		return u;
	}

	private int id;
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}

	//TODO better dirty
	public void updateTotalClues(int newValue) {
		totalClueCount = newValue;
		UserObserver.broadcastUserChange();	
	}

	private static User currentUser;
	public static void setCurrentUser(User user) {
		currentUser = user;
	}

	public static User getCurrentUser() {
		return currentUser;
	}
	
	private static final String PREFS_LOGIN = "PREFS_LOGIN";
	private static final String CACHED_SOCIAL = "CACHED_SOCIAL";
	private static final String CACHED_TOKEN = "CACHED_TOKEN";
	private static final String CACHED_HOSTNAME = "CACHED_HOSTNAME";
	private static final String CACHED_FULL_NAME = "CACHED_FULL_NAME";
	private static final String CACHED_USER_ID = "CACHED_USER_ID";
	private static final String PREFS_LOCAL_SETTINGS = "PREFS_LOCAL_SETTINGS";
	
	public static void clearCachedCredentials(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		final String[] allPrefs = {CACHED_SOCIAL, CACHED_HOSTNAME, CACHED_FULL_NAME, CACHED_TOKEN, CACHED_USER_ID};
		for(String s: allPrefs)
			edit.putString(s, "");
		edit.commit();
	}
	
	public static String getCachedSocialProvider(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);		
		return prefs.getString(CACHED_SOCIAL, "");
	}
	
	public static void setCachedSocialProvider(Context context, SocialProvider provider) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putString(CACHED_SOCIAL, provider.getProviderName());
		edit.commit();
	}
	
	public static void setCachedCredentials(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		currentUser.serializeToPrefs(edit);	
		edit.commit();
	}

	private void serializeToPrefs(Editor edit) {
		edit.putString(CACHED_FULL_NAME, getFullName());
		edit.putString(CACHED_TOKEN, getApiToken());
		edit.putString(CACHED_HOSTNAME, getApiServerHostName());
		edit.putInt(CACHED_USER_ID, getID());
	}
	
	private void deserializeFromPrefs(SharedPreferences prefs) {
		fullName = prefs.getString(CACHED_FULL_NAME, null);
		apiToken = prefs.getString(CACHED_TOKEN, null);
		hostName = prefs.getString(CACHED_HOSTNAME, null);
		id = prefs.getInt(CACHED_USER_ID, -1);
		
		if(id == -1 || fullName == null || apiToken == null || hostName == null)
			throw new RuntimeException("Deserializing user object from empty prefs");
	}
	
	public static User getCachedUser(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);		
		if(prefs.contains(CACHED_TOKEN) && prefs.getString(CACHED_TOKEN, null).length() > 0) {
			User u = new User();
			u.deserializeFromPrefs(prefs);
			u.totalClueCount = UNKNOWN_CLUE_COUNT;
			return u;
		}
		else
			return null;
	}
	
	private boolean message_preview;

	public void setMessagePreview(boolean message_preview) {
		this.message_preview = message_preview;
		UserObserver.broadcastUserChange();
	}			
	
	public boolean getMessagePreview()
	{
		return message_preview;
	}
	
	private static final String LOCAL_VIBRATE_SETTING = "LOCAL_VIBRATE_SETTING";
	
	public boolean getVibratePref(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOCAL_SETTINGS, Context.MODE_PRIVATE);
		return prefs.getBoolean(LOCAL_VIBRATE_SETTING, true);
	}
	
	public void setVibratePref(Context context, boolean value) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOCAL_SETTINGS, Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putBoolean(LOCAL_VIBRATE_SETTING, value);
		edit.commit();
	}
}
