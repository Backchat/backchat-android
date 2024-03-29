package com.youtell.backchat.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.youtell.backchat.api.GetFeaturedRequest;
import com.youtell.backchat.api.GetFriendsRequest;
import com.youtell.backchat.api.GetGabsRequest;
import com.youtell.backchat.api.PostDeviceRequest;
import com.youtell.backchat.observers.UserObserver;
import com.youtell.backchat.services.APIService;
import com.youtell.backchat.social.SocialProvider;

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
	
	public int getTotalClueCount() {
		return totalClueCount; 
	}
	
	private String deviceID;
	public void setDeviceID(String id) {
		APIService.fire(new PostDeviceRequest(id));
		deviceID = id;
	}
	
	public String getDeviceID() {
		return deviceID;
	}

	private static final String TAG = "USER";
	
	public void deserialize(Bundle b) {		
		this.totalClueCount = b.getInt("totalClueCount");
		setApiServerHostName(b.getString("hostName"));
		setApiToken(b.getString("apiToken"));
		setFullName(b.getString("fullName"));
		this.message_preview = b.getBoolean("message_preview");
		setID(b.getInt("id"));
		setIsNewUser(b.getBoolean("isNewUser"));
		this.deviceID = b.getString("deviceID"); 
	}
	
	public void serialize(Bundle b) {
		Log.i(TAG, "serialized");
		b.putInt("totalClueCount", getTotalClueCount());
		b.putString("hostName", getApiServerHostName());
		b.putString("apiToken", getApiToken());
		b.putString("fullName", getFullName());
		b.putBoolean("message_preview", message_preview);
		b.putBoolean("isNewUser", isNewUser());
		b.putInt("id", id);
		b.putString("deviceID", getDeviceID());
	}
	
	public User clone() {
		User u = new User();
		u.apiToken = apiToken;
		u.fullName = fullName;
		u.hostName = hostName;
		u.id = id;
		u.totalClueCount = totalClueCount;
		return u;
	}
	
	private boolean isNewUser;
	
	public void setIsNewUser(boolean b) 
	{
		isNewUser = b;
	}
	
	public boolean isNewUser() {
		return isNewUser;
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
	private static final String LOCAL_SOUND_SETTING = "LOCAL_SOUND_SETTING";
	
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

	public void setTotalClueCount(int clue) {
		totalClueCount = clue;
	}

	public boolean getSoundPref(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOCAL_SETTINGS, Context.MODE_PRIVATE);
		return prefs.getBoolean(LOCAL_SOUND_SETTING, true);
	}

	public void setSoundPref(Context context, boolean checked) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_LOCAL_SETTINGS, Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putBoolean(LOCAL_SOUND_SETTING, checked);
		edit.commit();		
	}
}
