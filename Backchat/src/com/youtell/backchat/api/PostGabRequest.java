package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;
import com.youtell.backchat.services.APIService;

import android.os.Bundle;

public class PostGabRequest extends PostRequest {
	private TypedArgumentHandler<Gab> gab = new TypedArgumentHandler<Gab>(Gab.class, this);

	public PostGabRequest()
	{
	}
	
	public PostGabRequest(Gab g) {
		gab.setObject(g);
	}	

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("related_user_name", gab.object.getRelatedUserName()));
		args.add(new BasicNameValuePair("unread_count", Integer.valueOf(gab.object.getUnreadCount()).toString()));
		return args;
	}

	@Override
	protected String getPath() {
		return String.format("/gabs/%d", gab.object.getRemoteID());
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		JSONObject gabData = result.getJSONObject("gab");
		gab.object.inflate(gabData);
		gab.object.save();
		APIService.mixpanel.track("Tagged Thread", null);
	}

}
