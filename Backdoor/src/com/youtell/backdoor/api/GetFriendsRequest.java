package com.youtell.backdoor.api;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.Friend;

import android.os.Bundle;

public class GetFriendsRequest extends GetListRequest<Friend> {

	public GetFriendsRequest()
	{
		super(Friend.class);
	}
	
	@Override
	protected void addArguments(Bundle b) {
	}

	@Override
	protected void inflateArguments(Bundle args) {
	}

	@Override
	public HttpUriRequest getRequestURI() {
		return new HttpGet("/friends");
	}

	@Override
	protected JSONArray getJSONItemArray(JSONObject result) throws JSONException {
		return result.getJSONArray("friends");
	}

}
