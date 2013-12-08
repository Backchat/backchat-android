package com.youtell.backchat.observers;

import com.youtell.backchat.models.Clue;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ClueObserver extends LocalGabChildObjectObserver<Clue, ClueObserver.Observer> {
	public interface Observer extends LocalGabChildObjectObserver.Observer {
	}
	
	public ClueObserver(Observer observer, Gab g) {
		super(observer, g);		
	}
	
	public static final String CLUE_UPDATED = "CLUE_UPDATED"; /* updated from server side */
	public static final String CLUE_INSERTED = "CLUE_INSERTED"; /* inserted from server side */
	private static final String[] possibleActions = {CLUE_UPDATED, CLUE_INSERTED};
	
	protected String[] getPossibleActions() {
		return possibleActions;
	}
}
