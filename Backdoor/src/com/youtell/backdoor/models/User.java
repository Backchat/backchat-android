package com.youtell.backdoor.models;

import android.os.Bundle;
import android.util.Log;

import com.youtell.backdoor.api.GetFeaturedRequest;
import com.youtell.backdoor.api.GetFriendsRequest;
import com.youtell.backdoor.api.GetGabsRequest;
import com.youtell.backdoor.api.PostDeviceRequest;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.services.APIService;
import com.youtell.backdoor.social.SocialProvider;

public class User implements InflatableObject {

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

	private int totalClueCount;
	
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
		setTotalClueCount(b.getInt("totalClueCount"));
		setApiServerHostName(b.getString("hostName"));
		setApiToken(b.getString("apiToken"));
		setFullName(b.getString("fullName"));
		setID(b.getInt("id"));
	}
	
	public void serialize(Bundle b) {
		Log.i(TAG, "serialized");
		b.putString("GCMKey", getGCMKey());
		b.putInt("totalClueCount", getTotalClueCount());
		b.putString("hostName", getApiServerHostName());
		b.putString("apiToken", getApiToken());
		b.putString("fullName", getFullName());
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
}
