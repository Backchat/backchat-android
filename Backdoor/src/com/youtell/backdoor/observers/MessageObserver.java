package com.youtell.backdoor.observers;

import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MessageObserver extends ModelObserver<MessageObserver.Observer> {
	public interface Observer {
		public void onChange(String action, int gabID, int messageID);
	}
	
	private int gabID;
	private static final int GAB_OBSERVE_ALL = -2;
	
	public MessageObserver(Observer observer, Gab g) {
		super(observer);
		gabID = GAB_OBSERVE_ALL;
		
		if(g != null) {
			gabID = g.getID();
		}
	}
	
	public static final String MESSAGE_UPDATED = "MESSAGE_UPDATED"; /* updated from server side */
	public static final String MESSAGE_INSERTED = "MESSAGE_INSERTED"; /* inserted from server side */
	public static final String MESSAGE_ADDED = "MESSAGE_ADDED"; /* isNew */
	private static final String[] possibleActions = {MESSAGE_ADDED, MESSAGE_UPDATED, MESSAGE_INSERTED};
	private static final String ARG_GAB_ID = "ARG_GAB_ID";
	private static final String ARG_MESSAGE_ID = "ARG_MESSAGE_ID";
	
	protected String[] getPossibleActions() {
		return possibleActions;
	}
	
	public static void broadcastChange(String action, Message m) {
		Bundle args = new Bundle();
		args.putInt(ARG_GAB_ID, m.getGab().getID());
		args.putInt(ARG_MESSAGE_ID, m.getID());
		ModelObserver.broadcastChange(action, args);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int thisGabID = intent.getIntExtra(ARG_GAB_ID, -1); //TODO
		int thisMessageID = intent.getIntExtra(ARG_MESSAGE_ID, -1);
		if(thisGabID == gabID || gabID == GAB_OBSERVE_ALL)
			observer.onChange(intent.getAction(), thisGabID, thisMessageID);
	}
}
