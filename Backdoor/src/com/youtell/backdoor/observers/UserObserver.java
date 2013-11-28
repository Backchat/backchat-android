package com.youtell.backdoor.observers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.youtell.backdoor.models.User;

public class UserObserver extends LocalObserver<UserObserver.Observer>  {
	public UserObserver(Observer observer) {
		super(observer);
	}
	
	public interface Observer extends LocalObserver.Observer {
		public void onUserChanged();
	}

	private final static String USER_CHANGED = "USER_CHANGED";

	private final static String[] possibleActions = {USER_CHANGED};

	@Override
	protected String[] getPossibleActions() {
		return possibleActions;
	}

	static public void broadcastUserChange() {
		broadcastChange(USER_CHANGED, new Bundle());
	}
		

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction() == USER_CHANGED) {				
			observer.onUserChanged();
		}
	}
}
