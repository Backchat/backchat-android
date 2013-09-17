package com.youtell.backdoor.observers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class GabObserver extends ModelObserver<GabObserver.Observer> {
	public interface Observer {
		public void onChange();
	}
	
	public GabObserver(Observer observer) {
		super(observer);
	}

	private static final String GAB_LIST_CHANGED = "GAB_LIST_CHANGED";
	private static final String[] possibleActions = {GAB_LIST_CHANGED};
	
	protected String[] getPossibleActions() {
		return possibleActions;
	}
	
	public static void broadcastChange() {
		broadcastChange(GAB_LIST_CHANGED, new Bundle());
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		observer.onChange();
	}

}
