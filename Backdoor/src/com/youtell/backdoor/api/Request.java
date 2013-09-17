package com.youtell.backdoor.api;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class Request {
	private static final String CLASS_NAME = "CLASS_NAME";
	
	protected abstract void addArguments(Bundle b);
	
	protected Request() {		
	}
	
	public Bundle getArguments() {
		Bundle bundle = new Bundle();
		bundle.putString(CLASS_NAME, this.getClass().getName());
		addArguments(bundle);
		return bundle;
	}
	
	abstract protected void inflateArguments(Bundle args);
	protected abstract List<NameValuePair> getParameters();
	protected abstract String getPath();

	@SuppressWarnings("unchecked")
	static public Request inflateRequest(Intent intent) {
		String className = intent.getStringExtra(CLASS_NAME);
		try {
			Request r = null;
			Class<? extends Request> c;
			c = (Class<? extends Request>) Class.forName(className);
			r = c.newInstance();
			r.inflateArguments(intent.getExtras());
			return r;
		} catch (Exception e) {
			Log.e("REQUEST", "EXCEPTION", e);
			return null;
		}
	}	

	//TODO error
	protected void handleResult(String result) {
		try {
			JSONObject json = new JSONObject(result);
			String status = json.getString("status");
			if(status.equals("ok")) {
				JSONObject resultJSON = json.getJSONObject("response");
				handleJSONResponse(resultJSON);
			}
			else 
			{
				Log.e("REQUEST", String.format("FAILED %s", status));
			}
		} catch (JSONException e) {
			Log.e("REQUEST", "EXCEPTION", e);
		}
		
	}

	abstract public void execute(HttpClient client, User user); 
	abstract protected void handleJSONResponse(JSONObject result) throws JSONException;
}
