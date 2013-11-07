package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.User;

public class GetUserSettingsRequest extends GetRequest {

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		return args;
	}

	@Override
	protected String getPath() {
		return "/";
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user)
			throws JSONException {
		JSONObject userobj = result.getJSONObject("user");
		JSONObject settings = userobj.getJSONObject("settings");
		boolean message_preview = settings.getBoolean("message_preview");
		
		user.setMessagePreview(message_preview);
	}

}
