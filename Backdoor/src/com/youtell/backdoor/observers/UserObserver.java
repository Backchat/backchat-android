package com.youtell.backdoor.observers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.youtell.backdoor.models.User;

public class UserObserver {
	public interface Observer {
		public void onUserChanged();
		public void onUserSwapped(User old, User newUser);
	}
		
	private static class PrivateUserObserver extends LocalObserver<Observer> {
		public PrivateUserObserver(Observer observer) {
			super(observer);			
		}

		private static String USER_CHANGED = "USER_CHANGED";
		private static String USER_SWAPPED = "USER_SWAPPED";
		private static String USER_REMOVED = "USER_REMOVED";
		
		private static String[] possibleActions = {USER_CHANGED, USER_SWAPPED, USER_REMOVED};
		
		@Override
		protected String[] getPossibleActions() {
			return possibleActions;
		}
		
		static public void broadcastUserSwapped(User newUser)
		{
			if(newUser != null) {
				Bundle bundle = new Bundle();
				newUser.serialize(bundle);
				broadcastChange(USER_SWAPPED, bundle);
			}
			else
				broadcastChange(USER_REMOVED, new Bundle());
		}
		
		static public void broadcastUserChange(User newUser) {
			Bundle bundle = new Bundle();
			newUser.serialize(bundle);
			broadcastChange(USER_CHANGED, bundle);
		}
		
		private User user;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction() == USER_CHANGED) {
				if(user == null) {
					user = new User(); 
					Log.e("USEROBSERVER", "cahnged in null: swap not called");
				}
				user.deserialize(intent.getExtras());
				if(observer != null)
					observer.onUserChanged();
			}
			else if(intent.getAction() == USER_SWAPPED) {
				User newUser = new User();
				newUser.deserialize(intent.getExtras());
				User oldUser = user;
				user = newUser;
				
				if(observer != null)
					observer.onUserSwapped(oldUser, newUser);
			}
			else {
				User oldUser = user;
				user = null;
				if(observer != null)
					observer.onUserSwapped(oldUser, null);
			}
		}
	}
		
	static private PrivateUserObserver staticObserver = new PrivateUserObserver(null);
	static {
		staticObserver.startListening();
	}
	
	static public Object registerObserver(Observer v) {
		PrivateUserObserver ob = new PrivateUserObserver(v);
		if(staticObserver.user != null) {
			Bundle bundle = new Bundle();
			staticObserver.user.serialize(bundle);
			Intent intent = new Intent(PrivateUserObserver.USER_SWAPPED);
			intent.putExtras(bundle);
			ob.onReceive(null, intent);
		}
		else {
			Intent intent = new Intent(PrivateUserObserver.USER_REMOVED);
			ob.onReceive(null, intent);
		}
		ob.startListening();
		return ob;
	}
	
	static public void unregisterObserver(Object observingObject) {
		((PrivateUserObserver)observingObject).stopListening();
	}
	
	static public void broadcastUserSwapped(User newUser)
	{
		PrivateUserObserver.broadcastUserSwapped(newUser);
	}
	
	static public void broadcastUserChange(User newUser) {
		PrivateUserObserver.broadcastUserChange(newUser);
	}
}
