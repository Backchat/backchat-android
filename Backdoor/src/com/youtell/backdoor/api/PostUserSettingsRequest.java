package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.User;

public class PostUserSettingsRequest extends PostRequest {

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		
		//TODO better?
		String messagePreview = User.getCurrentUser().getMessagePreview() ? "true" : "false";
		args.add(new BasicNameValuePair("settings[message_preview]", messagePreview));
		
		return args;
	}

	@Override
	protected String getPath() {
		return "/";
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user)
			throws JSONException {
		//assume always OK TODO
	}

}
