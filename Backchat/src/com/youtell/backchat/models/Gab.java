package com.youtell.backchat.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.youtell.backchat.Util;
import com.youtell.backchat.api.DeleteGabRequest;
import com.youtell.backchat.api.GetGabCluesRequest;
import com.youtell.backchat.api.GetGabMessagesRequest;
import com.youtell.backchat.api.PostGabRequest;
import com.youtell.backchat.observers.ClueObserver;
import com.youtell.backchat.observers.GabObserver;
import com.youtell.backchat.observers.MessageObserver;
import com.youtell.backchat.services.APIService;

//TODO dirty handling
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
		
	@ForeignCollectionField(orderColumnName="created_at", orderAscending=true)
	private ForeignCollection<Message> messages;
	
	@ForeignCollectionField
	private ForeignCollection<Clue> clues;
	
	@DatabaseField
	private boolean sent; //actually whether it is anon or not, e.g. "whether it was sent by us"
	
	@DatabaseField
	private boolean deleted;
	
	/* this is cached locally only during new gab */
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "related_friend_id")
	private Friend relatedFriend;
	
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

	public ForeignCollection<Clue> getClues() {
		return clues;		
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
	
	public boolean isRemoteObject() {
		return  remote_id != DatabaseObject.NEW_OBJECT && remote_id != DatabaseObject.REQUESTED_OBJECT && deleted == false;
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
	
	public void addClue(Clue c) {
		clues.add(c);
		if(c.isNew())
			ClueObserver.broadcastChange(ClueObserver.CLUE_INSERTED, c);
		else
			ClueObserver.broadcastChange(ClueObserver.CLUE_UPDATED, c);
	}

	public static Gab getByID(int gabID) {
		try {
			return getDAO().queryForId(gabID);
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
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
		setRelatedFriend(null); //clear the related friend info after inflation.
		deleted = false;
	}

	public void save() {
		try {
			getDAO().createOrUpdate(this);
			GabObserver.broadcastChange(GabObserver.GAB_UPDATED, this);				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.v("DAO", "Write gab", e);
		}
	}

	public void remove() {
		GabObserver.broadcastChange(GabObserver.GAB_DELETED, this);
	}
	
	/* TODO move these private interface */

	public void delete() {
		try {
			getDAO().delete(this);	
		}
		catch (SQLException e) {
			throw new RuntimeException("getDAO exception", e);
		}	
	}
	
	public void saveWithoutNotifications() {
		try {
			getDAO().createOrUpdate(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.v("DAO", "Write gab", e);
		}
	}
	
	public static List<Gab> allVisible() {
		try {
			return getDAO().queryBuilder().orderBy("updated_at", false).where().eq("deleted", false).query();
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
		if(isRemoteObject())
			APIService.fire(new GetGabMessagesRequest(this));
	}

	public void updateClues() {
		if(isRemoteObject())
			APIService.fire(new GetGabCluesRequest(this));
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

	public Message getMessageByID(int ID) {
		try {
			return getDB().messageDAO.queryForId(ID);
		}
		catch(SQLException e) {
			return null;
		}
	}

	public static void markDeletedNotIn(List<Integer> remoteIDs) {
		String query;
		query = "UPDATE GABS SET DELETED=1 WHERE REMOTE_ID NOT IN (-1, -2";
		if(!remoteIDs.isEmpty()) {
			query += ", " + TextUtils.join(", ", remoteIDs);
		}	
		query += ")";
		
		try {
			getDAO().executeRaw(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isUnread() {
		return getUnreadCount() != 0;
	}

	public Message getFirstMessage() {
		try {
			Message m = null;
			CloseableWrappedIterable<Message> it = getMessages().getWrappedIterable();		
			m = it.iterator().next();
			it.close();
			return m;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setRelatedFriend(Friend f) {
		relatedFriend = f;
	}
	
	public Friend getRelatedFriend() {
		return relatedFriend;
	}

	
	//TODO change to observer & dirty
	public void updateTag() {
		if(isRemoteObject())		
			APIService.fire(new PostGabRequest(this));
	}
	
	public void updateUnread() {
		if(isRemoteObject())
			GabObserver.broadcastChange(GabObserver.GAB_UNREAD_COUNT_CHANGED, this);
	}

	public void refresh() {
		try {
			getDAO().refresh(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Clue getClueByRemoteID(int remoteID) {
		List<Clue> clues;
		try {
			clues = getDB().clueDao.queryBuilder().where().eq("remote_id", remoteID).and().eq("gab_id", getID()).query();
			if(clues.size() != 1)
				return null;
			else
				return clues.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Clue getClueByID(int objectID) {
		try {
			return getDB().clueDao.queryForId(objectID);
		}
		catch(SQLException e) {
			return null;
		}
	}

	public void setDeleted(boolean b) {
		deleted = b;		
	}
}
