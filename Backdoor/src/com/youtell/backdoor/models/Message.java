package com.youtell.backdoor.models;

import java.sql.SQLException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.youtell.backdoor.Util;
import com.youtell.backdoor.observers.MessageObserver;

@DatabaseTable(tableName = "messages")
public class Message extends DatabaseObject {
	public static final int KIND_TEXT = 0;
	public static final int KIND_IMAGE = 1;
	
	private Dao<Message, Integer> getDAO() {
		return getDB().messageDAO;
	}
	
	@DatabaseField
	private String content;
	@DatabaseField	
	private boolean sent; //acutally whether it was sent by us, e.g. isMine.
	@DatabaseField
	private Date created_at;	
	
	@DatabaseField
	private int kind;
	@DatabaseField
	private String key;
	
	@DatabaseField(generatedId = true)
	int id;
	
	@DatabaseField(index = true)
	int remote_id;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "gab_id")
	private Gab gab;
		
	public Message()
	{
	}	
	
	public boolean isMine() {
		return sent;
	}
	
	public boolean isSent() {
		return !isNew();
	}

	public Date getCreatedAt() {
		return created_at;
	}

	public void setText(String string) {
		content = string;
		kind = KIND_TEXT;
	}

	public void setMine(boolean b) {
		sent = b;		
	}

	public void setCreatedAt(Date date) {
		created_at = date;		
	}

	@Override
	public int getID() {
		return id;
	}

	public Gab getGab() {
		return gab;
	}

	@Override
	public int getRemoteID() {
		return remote_id;
	}

	@Override
	public void setRemoteID(int i) {
		remote_id = i;
	}

	@Override
	public void inflate(JSONObject j) throws JSONException {
		setKey(j.getString("key"));
		setContent(j.getString("content"));
		//setGab!! TODO
		setKind(j.getInt("kind"));
		setMine(j.getBoolean("sent"));
		setCreatedAt(Util.parseJSONDate(j.getString("created_at")));
	}
	
	public void setKey(String s) {
		key = s;
	}
	
	public void setContent(String s) {
		content = s;
	}
	
	public void setKind(int k) {
		kind = k;
	}	
	
	public int getKind() {
		return kind;
	}
	
	public String getContent() {
		return content;
	}
	
	public boolean isNew() {
		return getRemoteID() == DatabaseObject.NEW_OBJECT;
	}
	
	@Override
	public void save() {
		try {
			getDAO().createOrUpdate(this);
			MessageObserver.broadcastChange(MessageObserver.MESSAGE_UPDATED, this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
