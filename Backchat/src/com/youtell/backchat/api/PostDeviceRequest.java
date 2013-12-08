package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.models.User;

import android.os.Bundle;

//TODO merge string,int to simplehandler
public class PostDeviceRequest extends PostRequest {
	private StringArgumentHandler deviceToken = new StringArgumentHandler("deviceToken", this);
	
	public PostDeviceRequest() {}
	
	public PostDeviceRequest(String id) {
		deviceToken.content = id;
	}	
	
	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("device_token", deviceToken.content));
		params.add(new BasicNameValuePair("kind", "GOOGLE"));
		return params;
	}

	@Override
	protected String getPath() {
		return "/devices";
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		//nothing.
	}

}
