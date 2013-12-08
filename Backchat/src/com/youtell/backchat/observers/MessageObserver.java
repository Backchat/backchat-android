package com.youtell.backchat.observers;

import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MessageObserver extends LocalGabChildObjectObserver<Message, MessageObserver.Observer> {
	public interface Observer extends LocalGabChildObjectObserver.Observer {
	}

	public MessageObserver(Observer observer, Gab g) {
		super(observer, g);
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
}
