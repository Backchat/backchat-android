package com.youtell.backchat.api;

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

import com.youtell.backchat.Settings;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.APIRequestObserver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class Request {
	private static final String CLASS_NAME = "CLASS_NAME";
	private static final String REQUEST_ID = "REQUEST_ID";
	
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
		bundle.putInt(REQUEST_ID, requestID);
		addArguments(bundle);
		return bundle;
	}
	
	protected abstract List<NameValuePair> getParameters();
	protected abstract String getPath();
	protected int requestID;
	
	public int getRequestID() {
		return requestID;
	}
	
	public void setRequestID(int id) {
		requestID = id;
	}

	@SuppressWarnings("unchecked")
	static public Request inflateRequest(Bundle bundle) {
		String className = bundle.getString(CLASS_NAME);
		try {
			Request r = null;
			Class<? extends Request> c;
			c = (Class<? extends Request>) Class.forName(className);
			r = c.newInstance();
			r.inflateArguments(bundle);
			r.requestID = bundle.getInt(REQUEST_ID);
			return r;
		} catch (Exception e) {
			Log.e("REQUEST", String.format("EXCEPTION %s %s", className, bundle.toString()), e);
			return null;
		}
	}	

	@SuppressWarnings("unchecked")
	public static Class<? extends Request> inflateClassType(Bundle bundle) {
		String className = bundle.getString(CLASS_NAME);
		try {
			return (Class<? extends Request>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			Log.e("REQUEST", String.format("EXCEPTION %s %s", className, bundle.toString()), e);
			return null;
		}
	}
	
	protected void handleSuccess() {
		APIRequestObserver.broadcastSuccess(this);
	}
	
	//TODO error
	protected void handleResult(String result, User user) {
		try {
			JSONObject json = new JSONObject(result);
			String status = json.getString("status");
			if(status.equals("ok")) {
				JSONObject resultJSON = json.getJSONObject("response");
				handleJSONResponse(resultJSON, user);
				//success!
				handleSuccess();
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
			if(Settings.settings.slowInternet)
				Thread.sleep(Settings.internetDelay);
		}
		catch (Exception e) {
			handleInternetFailure();
			Log.e("APISERVER", String.format("ERROR %s", this.getClass().getName()), e);
			return;
		}

		//check to see if the current user is still the user we passed in originally
		if(user != null && 
				(User.getCurrentUser() == null || User.getCurrentUser().getID() != user.getID())) {
			Log.e("APISERVER", String.format("ignored %s because user is now NULL or != id",
					this.getClass().getName()));
			return;
		}
					
		handleResult(result, user);
	}
	
	protected Context context;
	
	abstract protected void handleJSONResponse(JSONObject result, User user) throws JSONException;

	public void setContext(Context applicationContext) {
		context = applicationContext;
	}
}
