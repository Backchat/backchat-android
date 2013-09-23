package com.youtell.backdoor.observers;

import com.youtell.backdoor.models.DatabaseGabObject;
import com.youtell.backdoor.models.DatabaseObject;
import com.youtell.backdoor.models.Gab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract class LocalGabChildObjectObserver<Model extends DatabaseObject, T> extends LocalObserver<T> {
	public static interface Observer {
		public void onChange(String action, int gabID, int objectID);
	}

	private static final String ARG_GAB_ID = "ARG_GAB_ID";
	private static final String ARG_OBJ_ID = "ARG_OBJ_ID";

	@Override
	public void onReceive(Context context, Intent intent) {
		int thisGabID = intent.getIntExtra(ARG_GAB_ID, -1); //TODO
		int thisObjID = intent.getIntExtra(ARG_OBJ_ID, -1);
		if(thisGabID == gabID || gabID == GAB_OBSERVE_ALL)
			((LocalGabChildObjectObserver.Observer)observer).onChange(intent.getAction(), thisGabID, thisObjID);
	}

	private int gabID;
	private static final int GAB_OBSERVE_ALL = -2;

	public LocalGabChildObjectObserver(T observer, Gab g) {
		super(observer);
		gabID = GAB_OBSERVE_ALL;

		if(g != null) {
			gabID = g.getID();
		}
	}
	
	public static <Model extends DatabaseGabObject> void broadcastChange(String action, Model m) {
		Bundle args = new Bundle();
		args.putInt(ARG_GAB_ID, m.getGab().getID());
		args.putInt(ARG_OBJ_ID, m.getID());
		LocalObserver.broadcastChange(action, args);
	}

}
