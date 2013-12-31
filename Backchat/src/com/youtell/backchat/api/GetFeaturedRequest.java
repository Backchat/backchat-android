package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.models.Friend;
import com.youtell.backchat.models.User;

public class GetFeaturedRequest extends GetListRequest<Friend> {

	public GetFeaturedRequest() {
		super(Friend.class);
	}
	
	@Override
	protected JSONArray getJSONItemArray(JSONObject result)
			throws JSONException {
		return result.getJSONArray("users");
	}

	@Override
	protected List<NameValuePair> getParameters() {
		return new ArrayList<NameValuePair>();
	}

	@Override
	protected String getPath() {
		return "/featured-users";
	}

	@Override
	protected void deleteObjects(List<Integer> remoteIDsTouched) throws JSONException {
		Friend.removeByNotRemoteIDs(remoteIDsTouched, true);		
	}
}
