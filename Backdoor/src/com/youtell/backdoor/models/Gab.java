package com.youtell.backdoor.models;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.youtell.backdoor.Util;
import com.youtell.backdoor.api.GetGabMessagesRequest;
import com.youtell.backdoor.observers.GabObserver;
import com.youtell.backdoor.observers.MessageObserver;
import com.youtell.backdoor.services.APIService;

@DatabaseTable(tableName = "gabs")
public class Gab extends DatabaseObject {	
	private static Dao<Gab, Integer> getDAO() {
		return getDB().gabDAO;
	}
	
	@DatabaseField
	private String related_user_name;
	
	@DatabaseField	
	private String related_avatar;
	
	@DatabaseField	
	private Date updated_at;
	
	@DatabaseField	
	private String content_summary;

	@DatabaseField
	private int clue_count;
	
	@DatabaseField
	private int unread_count;
	
	@DatabaseField
	private int total_count;
	
	@DatabaseField(generatedId = true)
	int id;
	
	@DatabaseField(index = true)
	int remote_id;
		
	@ForeignCollectionField
	private ForeignCollection<Message> messages;
	
	@DatabaseField
	private boolean sent; //actually whether it is anon or not, e.g. "whether it was sent by us"
	
	public Gab()
	{	
	}
	
	@Override
	public int getID() {
		return id;
	}
	
	public ForeignCollection<Message> getMessages() {
		return messages;
	}

	public boolean isAnonymous() {
		return !sent;
	}

	public void setIsAnonymous(boolean anon) {
		sent = !anon;
	}
	
	public String getRelatedUserName() {
		return related_user_name;
	}
	
	public void setRelatedUserName(String relatedUserName) {
		related_user_name = relatedUserName;
	}

	public String getContentSummary() {
		return content_summary;
	}
	
	public void setContentSummary(String s) {
		content_summary = s;
	}
	
	public String getRelatedAvatar() {
		return related_avatar;
	}
	
	public void setRelatedAvatar(String s) {
		related_avatar = s;
	}
	
	public Date getUpdatedAt() {
		return updated_at;		
	}

	public void setUpdatedAt(Date date) {
		updated_at = date;
	}

	public int getClueCount() {
		return clue_count;
	}
	
	public void setClueCount(int i) {
		clue_count = i;
	}
	
	public int getTotalCount() {
		return total_count;
	}
	
	public void setTotalCount(int i) {
		total_count = i;
	}
	
	public int getUnreadCount() {
		return unread_count;
	}
	
	public void setUnreadCount(int i) {
		unread_count = i;
	}
				
	public String getTitle() {
        String title = getRelatedUserName();
        if(title == null || title.isEmpty())
        	title = "???";
        
        return title;
	}

	public boolean isEmpty() {
		return messages.size() == 0;
	}

	public boolean isNew() {
		return remote_id == DatabaseObject.NEW_OBJECT;
	}

	public void setRemoteID(int remoteID) {
		remote_id = remoteID;
	}

	public void addMessage(Message m) {
		messages.add(m);
		if(m.isNew())		
			MessageObserver.broadcastChange(MessageObserver.MESSAGE_ADDED, m);
		else
			MessageObserver.broadcastChange(MessageObserver.MESSAGE_INSERTED, m);
	}

	public boolean isNewAndEmpty() {
		return isNew() && getMessages().size() == 0;
	}

	public static Gab getByID(int gabID) {
		try {
			return getDAO().queryForId(gabID);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Gab getByRemoteID(int remoteGabID) {
		try {
			List<Gab> results = getDAO().queryBuilder().where().eq("remote_id", remoteGabID).query();
			if(results.size() == 1) 
				return results.get(0);
			else
				return null;
		} catch(SQLException e) {
			return null;
		}
	}

	public void inflate(JSONObject gab) throws JSONException {
		setRelatedUserName(gab.getString("related_user_name"));
		setClueCount(gab.getInt("clue_count"));
		setContentSummary(gab.getString("content_summary"));
		setRelatedAvatar(gab.getString("related_avatar"));
		setIsAnonymous(!gab.getBoolean("sent"));
		setTotalCount(gab.getInt("total_count"));
		setUnreadCount(gab.getInt("unread_count"));		
		setUpdatedAt(Util.parseJSONDate(gab.getString("updated_at")));		
	}

	public void save() {
		try {
			getDAO().createOrUpdate(this);
			//TODO
			GabObserver.broadcastChange();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.v("DAO", "Write gab", e);
		}
	}

	public void remove() {
		try {
			getDAO().delete(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.v("DAO", "remove gab", e);
		}
	}

	public static List<Gab> all() {
		try {
			return getDAO().queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.v("DAO", "list gab", e);
			return new ArrayList<Gab>();
		}
	}

	@Override
	public int getRemoteID() {
		return remote_id;
	}

	public void updateWithMessages() {
		if(!isNew())
			APIService.fire(new GetGabMessagesRequest(this));
	}

	public Message getMessageByRemoteID(int remoteID) {
		List<Message> messages;
		try {
			messages = getDB().messageDAO.queryBuilder().where().eq("remote_id", remoteID).and().eq("gab_id", getID()).query();
			if(messages.size() != 1)
				return null;
			else
				return messages.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
