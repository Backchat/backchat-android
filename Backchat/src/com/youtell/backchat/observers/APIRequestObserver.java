package com.youtell.backchat.observers;

import com.youtell.backchat.api.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class APIRequestObserver<T extends Request> extends BaseLocalObserver<APIRequestObserver.Observer<T>> {
	public interface Observer<T extends Request> {
		public abstract void onFailure(T request);
		public abstract void onSuccess(T request);
	}
	
	private Class<T> clazz;
	
	public APIRequestObserver(Observer<T> observer, Class<T> clazz) {
		super(observer);
		this.clazz = clazz;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Context context, Intent intent) {
		String className = intent.getStringExtra(API_CLASSNAME);
		if(className.equals(this.clazz.getName())) {
			Bundle request = intent.getBundleExtra(REQUEST_BUNDLE);
			T r = (T) Request.inflateRequest(request);
			if(intent.getAction() == API_SUCCESS)
				observer.onSuccess(r);
			else
				observer.onFailure(r);
		}
			
	}

	private static final String API_SUCCESS = "API_SUCCESS";
	private static final String API_FAILURE = "API_FAILURE";
	private static final String API_CLASSNAME = "API_CLASSNAME";
	private static final String REQUEST_BUNDLE = "REQUEST_BUNDLE";
	
	private static final String[] possibleActions = {API_SUCCESS, API_FAILURE};
	
	@Override
	protected String[] getPossibleActions() {
		return possibleActions;
	}
	
	private static Bundle buildArgs(Request r) {
		Bundle b = new Bundle();
		b.putString(API_CLASSNAME, r.getClass().getName());
		Bundle request = r.getArguments();
		b.putBundle(REQUEST_BUNDLE, request);
		return b;
	}
	
	public static void broadcastSuccess(Request r) {
		broadcastChange(API_SUCCESS, buildArgs(r));
	}
	
	public static void broadcastFailure(Request r) {
		broadcastChange(API_FAILURE, buildArgs(r));
	}

}
