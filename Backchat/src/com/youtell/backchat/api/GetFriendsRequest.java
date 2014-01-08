package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.models.Friend;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.FriendObserver;

import android.os.Bundle;

public class GetFriendsRequest extends GetListRequest<Friend> {

	public GetFriendsRequest()
	{
		super(Friend.class);
	}
		
	@Override
	protected JSONArray getJSONItemArray(JSONObject result) throws JSONException {
		return result.getJSONArray("friends");
	}

	@Override
	protected List<NameValuePair> getParameters() {
		return new ArrayList<NameValuePair>();
	}

	@Override
	protected String getPath() {
		return "/friends";
	}
	
	@Override
	protected void deleteObjects(List<Integer> remoteIDsTouched) throws JSONException {
		Friend.removeByNotRemoteIDs(remoteIDsTouched, false);		
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		super.handleJSONResponse(result, user);
		FriendObserver.broadcastChange();
	}
}
