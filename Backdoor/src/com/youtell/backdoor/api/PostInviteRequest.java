package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.observers.APIRequestObserver;

import android.os.Bundle;
import android.os.SystemClock;

public class PostInviteRequest extends PostRequest {
	private ArrayList<String> phoneNumbers;
	private String message;
	
	private static final String ARG_MESSAGE = "ARG_MESSAGE";
	private static final String ARG_PHONE_NUMBERS = "ARG_PHONE_NUMBERS";
	
	public PostInviteRequest()
	{	
	}
	
	public PostInviteRequest(ArrayList<String> numbers, String msg) {
		message = msg;
		phoneNumbers = numbers;		
	}
	
	@Override
	protected void addArguments(Bundle b) {
		b.putString(ARG_MESSAGE, message);
		b.putStringArrayList(ARG_PHONE_NUMBERS, phoneNumbers);
	}

	@Override
	protected void inflateArguments(Bundle args) {
		phoneNumbers = args.getStringArrayList(ARG_PHONE_NUMBERS);
		message = args.getString(ARG_MESSAGE);
	}

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("invite[body]", message));
		for(String number : phoneNumbers)
			params.add(new BasicNameValuePair("contact[phone_numbers][]",	number));
		
		return params;
	}

	@Override
	protected String getPath() {
		return "/invites";
	}

	@Override
	protected void handleJSONResponse(JSONObject result) throws JSONException {
		// succeeded, empty data just "ok" status
	}


}
