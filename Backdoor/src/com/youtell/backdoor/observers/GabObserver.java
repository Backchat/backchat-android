package com.youtell.backdoor.observers;

import com.youtell.backdoor.models.Gab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class GabObserver extends LocalObserver<GabObserver.Observer> {
	public interface Observer {
		public void onChange(String action, int gabID);
	}
	
	public GabObserver(Observer observer) {
		super(observer);
		gabID = GAB_OBSERVE_ALL;
	}

	private int gabID;
	private static final int GAB_OBSERVE_ALL = -2;

	public GabObserver(Observer observer, Gab gab) {
		super(observer);
		this.gabID = gab.getID();
	}

	public static final String GAB_UPDATED = "GAB_UPDATED"; /* updated from server side */
	public static final String GAB_DELETED = "GAB_DELETED"; /* deleted from client side */
	public static final String GAB_INSERTED = "GAB_INSERTED"; /* gab went from isNew() -> inserted from server side */

	private static final String ARG_GAB_ID = "ARG_GAB_ID";
	
	private static final String[] possibleActions = {GAB_UPDATED, GAB_DELETED, GAB_INSERTED};
	
	protected String[] getPossibleActions() {
		return possibleActions;
	}
	
	public static void broadcastChange(String action, Gab g) {
		Bundle b = new Bundle();
		b.putInt(ARG_GAB_ID, g.getID());
		broadcastChange(action, b);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		int theGabID = intent.getIntExtra(ARG_GAB_ID, -1); //TODO
		if(theGabID == this.gabID || this.gabID == GAB_OBSERVE_ALL)
			observer.onChange(intent.getAction(), theGabID);
	}

}
