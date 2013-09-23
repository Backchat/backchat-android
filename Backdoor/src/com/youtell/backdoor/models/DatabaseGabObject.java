package com.youtell.backdoor.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

public abstract class DatabaseGabObject extends DatabaseObject {

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "gab_id")
	private Gab gab;
	
	public void setGab(Gab gab) {
		this.gab = gab;
	}

	public Gab getGab() {
		return gab;
	}
}
