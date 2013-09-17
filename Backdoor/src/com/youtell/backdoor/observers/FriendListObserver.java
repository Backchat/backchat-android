package com.youtell.backdoor.observers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class FriendListObserver extends ModelObserver<FriendListObserver.Observer> {
	public interface Observer {
		public void onChange();
	}
	
	public FriendListObserver(Observer observer) {
		super(observer);
	}

	private static final String FRIENDS_LIST_CHANGED = "FRIENDS_LIST_CHANGED";
	private static final String[] possibleActions = {FRIENDS_LIST_CHANGED};
	
	protected String[] getPossibleActions() {
		return possibleActions;
	}
	
	public static void broadcastChange() {
		ModelObserver.broadcastChange(FRIENDS_LIST_CHANGED, new Bundle());
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		observer.onChange();
	}

}
