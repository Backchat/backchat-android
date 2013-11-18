package com.youtell.backdoor.models;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.youtell.backdoor.observers.ClueObserver;
import com.youtell.backdoor.observers.MessageObserver;

@DatabaseTable(tableName = "clues")
public class Clue extends DatabaseGabObject {

	private static Dao<Clue, Integer> getDAO() {
		return getDB().clueDao;
	}

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private int remote_id;

	@DatabaseField
	private String field;

	@DatabaseField
	private	String value;

	@DatabaseField
	private int number;

	public Clue() {	
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public int getRemoteID() {
		return remote_id;
	}

	@Override
	public void setRemoteID(int i) {
		remote_id = i;
	}

	public void setValue(String s) {
		value = s;
	}
	
	public void setField(String s) {
		field = s;
	}
	
	public void setNumber(int n) {
		number = n;
	}
	
	@Override
	public void inflate(JSONObject j) throws JSONException {
		setValue(j.getString("value"));
		setField(j.getString("field"));
		setNumber(j.getInt("number"));
	}

	@Override
	public void save() {
		try {
			getDAO().createOrUpdate(this);
			ClueObserver.broadcastChange(ClueObserver.CLUE_UPDATED, this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refresh() {
		try {
			getDAO().refresh(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getNumber() {
		return number;
	}

	public String getField() {
		return field;
	}
	
	public String getValue() {
		return value;
	}
	
	static public Clue getByID(int id) {
		try {
			return getDAO().queryForId(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	static public Clue getByRemoteID(int remoteID) {
		List<Clue> clues;
		try {
			clues = getDB().clueDao.queryBuilder().where().eq("remote_id", remoteID).query();
			if(clues.size() != 1)
				return null;
			else
				return clues.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isNew() {
		return getRemoteID() == DatabaseObject.NEW_OBJECT;
	}

	//TODO efficiency split earlier in inflate?
	public String getDisplayText(Context context) {
		int pos = getValue().indexOf("|");
		String title = getValue().substring(pos+1);
		
		String translation_tag = String.format("clue_translated_%s", getField());
		Resources res = context.getResources();
		String packageName = context.getPackageName();
		int title_resid = res.getIdentifier(translation_tag, "string", packageName);
		if(title_resid != 0) {
			String translated_title = res.getString(title_resid);
			return String.format("%s: %s", translated_title, title);
		}
		else {
			return title;
		}
	}
	
	public String getURL()
	{
		int pos = getValue().indexOf("|");		
		return getValue().substring(0, pos);
	}
}
