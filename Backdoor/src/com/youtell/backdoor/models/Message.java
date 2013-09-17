package com.youtell.backdoor.models;

import java.sql.SQLException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messages")
public class Message extends DatabaseObject {
	private Dao<Message, Integer> getDAO() {
		return getDB().messageDAO;
	}
	
	@DatabaseField
	public String text;
	@DatabaseField	
	private boolean sent; //acutally whether it was sent by us, e.g. isMine.
	@DatabaseField
	private Date created_at;	
	/* really, this is only client side. we set it to false, and when we get
	 * the ack back, we set it to true.
	 */
	@DatabaseField
	private boolean state; //whether it is delivered or not.
	
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
		return state;
	}

	public Date getCreatedAt() {
		return created_at;
	}

	public void setText(String string) {
		text = string;
	}

	public void setMine(boolean b) {
		sent = b;		
	}

	public void setCreatedAt(Date date) {
		created_at = date;		
	}

	public void setSent(boolean b) {
		state = b;		
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
		// TODO Auto-generated method stub		
	}

	@Override
	public void save() {
		try {
			getDAO().createOrUpdate(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
