package com.youtell.backdoor.observers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class InviteObserver extends ModelObserver<InviteObserver.Observer> {
	public interface Observer {
		void onInviteFinished(boolean succeeded);
	}
	
	public InviteObserver(Observer observer) {
		super(observer);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		observer.onInviteFinished(intent.getAction() == INVITE_SUCCEEDED);
	}

	public static final String INVITE_SUCCEEDED = "INVITE_SUCCEEDED";
	public static final String INVITE_FAILED = "INVITE_FAILED";
	
	private static final String[] possibleActions = {INVITE_SUCCEEDED, INVITE_FAILED};
	
	@Override
	protected String[] getPossibleActions() {
		return possibleActions;
	}
	
	public static void broadcastChange(String action) {
		broadcastChange(action, new Bundle());
	}

}
