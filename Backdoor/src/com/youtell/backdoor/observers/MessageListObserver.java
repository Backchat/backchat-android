package com.youtell.backdoor.observers;

import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MessageListObserver extends ModelObserver<MessageListObserver.Observer> {
	public interface Observer {
		public void onChange(String action, int gabID, int messageID);
	}
	
	private int gabID;
	
	public MessageListObserver(Observer observer, Gab g) {
		super(observer);
		gabID = -1;  // TODO
		
		if(g != null) {
			gabID = g.getID();
		}
	}
	
	private static final String MESSAGE_UPDATED = "MESSAGE_UPDATED";
	public static final String MESSAGE_ADDED = "MESSAGE_ADDED";
	private static final String[] possibleActions = {MESSAGE_ADDED, MESSAGE_UPDATED};
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
		if(thisGabID == gabID || gabID == -1)
			observer.onChange(intent.getAction(), thisMessageID, thisMessageID);
	}
}
