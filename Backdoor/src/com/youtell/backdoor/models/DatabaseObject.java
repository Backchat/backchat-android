package com.youtell.backdoor.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.otto.Subscribe;

public abstract class DatabaseObject {
	private static class DBListener {
		protected Database db;
		
		@Subscribe public void dbIsAvailable(Database theDB) 
		{
			db = theDB;
		}

		@Subscribe public void dbIsGone(DBClosedEvent e) {
			db = null;
		}
		
		public DBListener() {
			register();
		}
		
		public void register() {
			ModelBus.events.register(this);
		}
		
		public void unregister() {
			ModelBus.events.unregister(this);
		}
	}

	public static final int NEW_OBJECT = -1;
	
	private static DBListener db = new DBListener();
	
	protected static Database getDB() {
		return db.db;
	}
	
	public abstract int getID();
	public abstract int getRemoteID();
	public abstract void setRemoteID(int i);
	
	public abstract void inflate(JSONObject j) throws JSONException;
	public abstract void save();
	public abstract void refresh();
}
