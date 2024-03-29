package com.youtell.backchat.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.youtell.backchat.observers.FriendObserver;

@DatabaseTable(tableName = "friends")
public class Friend extends DatabaseObject {
	static private Dao<Friend, Integer> getDAO() {
		return getDB().friendDAO;
	}
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private int remote_id;
	@DatabaseField	
	private String first_name;
	@DatabaseField
	private String last_name;
	@DatabaseField
	private int featured_user_id;
	@DatabaseField
	private int user_id;
	@DatabaseField
	private String provider;
	@DatabaseField
	private String social_id;
	@DatabaseField
	private boolean isFeatured;
	@DatabaseField
	private boolean deleted;
	
	public Friend() {}

	@Override
	public int getID() { 
		return id;
	}

	public String getFullName() {
		return String.format("%s %s", this.first_name, this.last_name);
	}

	public Gab createNewGab() {
		Gab g = new Gab();
		g.setRemoteID(DatabaseObject.NEW_OBJECT);
		g.setRelatedUserName(getFullName());
		g.setRelatedAvatar(getAvatar());
		g.setUpdatedAt(new Date());
		g.setIsAnonymous(false);
		g.setRelatedFriend(this);
		return g;
	}

	public String getAvatar() {
		if(getProvider().equals("facebook"))
			return String.format("https://graph.facebook.com/%s/picture?width=90&height=90", getSocialID());
		else
			return String.format("http://profiles.google.com/s2/photos/profile/%s?sz=90", getSocialID());
	}

	private String getSocialID() {
		return social_id;
	}

	private String getProvider() {
		return provider;
	}

	public void setFirstName(String string) {
		first_name = string;
	}
	
	public void setLastName(String string) {
		last_name = string;
	}

	public static List<Friend> allFriends() {
		try {
			return getDAO().queryBuilder().orderBy("first_name", true).where().eq("isFeatured", false).and().eq("deleted",false).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Friend>();
		}
	}
	
	public static List<Friend> allFeatured() {
		try {
			return getDAO().queryBuilder().orderBy("first_name", true).where().eq("isFeatured", true).and().eq("deleted", false).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Friend>();
		}
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
		setFirstName(j.getString("first_name"));
		setLastName(j.getString("last_name"));
		if(j.has("friend_id")) {
			setFeatured(false);
		}
		else {
			setFeatured(true);
			setFeaturedUserID(j.getInt("featured_id"));
		}
		
		deleted = false;
		
		//TODO unused setUserID(j.getInt("user_id"));
		setProvider(j.getString("provider"));
		setSocialID(j.getString("social_id"));
	}

	private void setFeatured(boolean b) {
		isFeatured = b;
	}
	
	public boolean getFeatured() {
		return isFeatured;
	}

	public void setProvider(String s) {
		provider = s;
	}
	
	public void setSocialID(String s) {
		social_id = s;
	}
	
	public void setFeaturedUserID(int i) {
		featured_user_id = i;
	}
	
	public int getFeaturedUserID() {
		return featured_user_id;
	}
	
	public void setUserID(int i) {
		user_id = i;
	}
	
	static public Friend getByRemoteID(int id) {
		try {
			List<Friend> results = getDAO().queryBuilder().where().eq("remote_id", id).query();
			if(results.size() == 1) 
				return results.get(0);
			else
				return null;
		} catch(SQLException e) {
			return null;
		}
	}
	
	@Override
	public void save() {
		try {
			getDAO().createOrUpdate(this);
			//TODO	FriendObserver.broadcastChange(); for fthis friend
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Friend getByID(int friendID) {
		try {
			return getDAO().queryForId(friendID);
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}		
	}

	public static void markDeletedNotIn(List<Integer> remoteIDs, boolean isFeatured) {
		String query;
		query = "UPDATE FRIENDS SET deleted=1 WHERE ";

		if(!remoteIDs.isEmpty()) {
			query += "REMOTE_ID NOT IN (-1, -2, ";
			query += TextUtils.join(", ", remoteIDs);
			query += ") AND";
		}	
		
		//no boolean in sqlite		
		query += String.format(" ISFEATURED=%s", isFeatured?"1":"0");

		try {
			getDAO().executeRaw(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isFeatured() {
		return isFeatured;
	}
	
	public void refresh() {
		try {
			getDAO().refresh(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
