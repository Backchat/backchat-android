package com.youtell.backdoor.api;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.APIRequestObserver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class Request {
	private static final String CLASS_NAME = "CLASS_NAME";
	private ArrayList<WeakReference<ArgumentHandler>> handlers = new ArrayList<WeakReference<ArgumentHandler>>();
		
	protected void addArguments(Bundle b) {
		for(WeakReference<ArgumentHandler> handler : handlers) {
			handler.get().addArguments(b);
		}
	}

	protected void inflateArguments(Bundle args) {
		for(WeakReference<ArgumentHandler> handler : handlers) {
			handler.get().inflateArguments(args);
		}
	}
	
	public void addArgumentHandler(ArgumentHandler h) {
		handlers.add(new WeakReference<ArgumentHandler>(h));
	}
	
	protected Request() {		
	}
	
	public Bundle getArguments() {
		Bundle bundle = new Bundle();
		bundle.putString(CLASS_NAME, this.getClass().getName());
		addArguments(bundle);
		return bundle;
	}
	
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
				//success!
				APIRequestObserver.broadcastSuccess(this);
			}
			else 
			{
				handleServerFailure();
				Log.e("REQUEST", String.format("FAILED %s", status));
			}
		} catch (JSONException e) {
			handleParsingFailure();
			Log.e("REQUEST", "EXCEPTION", e);
		}
		
	}

	abstract protected HttpUriRequest getRequest(User user) throws Exception;
	
	protected void handleServerFailure() {
		APIRequestObserver.broadcastFailure(this);
	}
	
	protected void handleParsingFailure() {
		APIRequestObserver.broadcastFailure(this);
	}
	
	protected void handleInternetFailure() {
		APIRequestObserver.broadcastFailure(this);
	}
	
	public void execute(HttpClient client, User user) {
		String result;

		BasicResponseHandler handler = new BasicResponseHandler();

		try {
			result = client.execute(getRequest(user), handler);
		} catch (Exception e) {
			handleInternetFailure();
			Log.e("APISERVER", "ERROR", e);
			return;
		}

		handleResult(result);
	}
	
	abstract protected void handleJSONResponse(JSONObject result) throws JSONException;
}
