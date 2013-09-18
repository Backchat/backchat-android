package com.youtell.backdoor.observers;

import com.youtell.backdoor.api.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class APIRequestObserver<T extends Request> extends LocalObserver<APIRequestObserver.Observer<T>> {
	public interface Observer<T extends Request> {
		void onSuccess();
		void onFailure();
	}
	
	private Class<T> clazz;
	
	public APIRequestObserver(Observer<T> observer, Class<T> clazz) {
		super(observer);
		this.clazz = clazz;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String className = intent.getStringExtra(API_CLASSNAME);
		if(className.equals(this.clazz.getName())) {
			if(intent.getAction() == API_SUCCESS)
				observer.onSuccess();
			else
				observer.onFailure();
		}
			
	}

	private static final String API_SUCCESS = "API_SUCCESS";
	private static final String API_FAILURE = "API_FAILURE";
	private static final String API_CLASSNAME = "API_CLASSNAME";
	
	private static final String[] possibleActions = {API_SUCCESS, API_FAILURE};
	
	@Override
	protected String[] getPossibleActions() {
		return possibleActions;
	}
	
	private static Bundle buildArgs(Request r) {
		Bundle b = new Bundle();
		b.putString(API_CLASSNAME, r.getClass().getName());
		return b;
	}
	
	public static void broadcastSuccess(Request r) {
		broadcastChange(API_SUCCESS, buildArgs(r));
	}
	
	public static void broadcastFailure(Request r) {
		broadcastChange(API_FAILURE, buildArgs(r));
	}

}
