package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
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
	protected void handleServerFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleParsingFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleInternetFailure() {
		// TODO Auto-generated method stub
		
	}

}
