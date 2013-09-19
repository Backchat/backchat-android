package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public class PostDeviceRequest extends PostRequest {
	private String deviceToken;
	private final static String DEVICE_TOKEN_ARG = "DEVICE_TOKEN_ARG";
	
	public PostDeviceRequest() {}
	
	public PostDeviceRequest(String id) {
		deviceToken = id;
	}
	
	@Override 
	protected void addArguments(Bundle b) {
		b.putString(DEVICE_TOKEN_ARG, deviceToken);
	}
	
	@Override
	protected void inflateArguments(Bundle args) {
		deviceToken = args.getString(DEVICE_TOKEN_ARG);
	}
	
	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("device_token", deviceToken));
		params.add(new BasicNameValuePair("kind", "GOOGLE"));
		return params;
	}

	@Override
	protected String getPath() {
		return "/devices";
	}

	@Override
	protected void handleJSONResponse(JSONObject result) throws JSONException {
		//nothing.
	}

}
