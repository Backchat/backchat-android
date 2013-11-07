package com.youtell.backdoor.models;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.youtell.backdoor.Util;
import com.youtell.backdoor.observers.MessageObserver;

@DatabaseTable(tableName = "messages")
public class Message extends DatabaseGabObject {
	public static final int KIND_TEXT = 0;
	public static final int KIND_IMAGE = 1;
	public static final int KIND_IMAGE_PATH = 2;
	
	private static Dao<Message, Integer> getDAO() {
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
	
	@DatabaseField
	private String secret;
	
	@DatabaseField(generatedId = true)
	int id;
	
	@DatabaseField(index = true)
	int remote_id;
	
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
		setKind(KIND_TEXT);
	}
	
	public void setMine(boolean b) {
		sent = b;		
	}

	public void setCreatedAt(Date date) {
		created_at = date;		
	}

	public void setSecret(String s) {
		secret = s;
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

	@Override
	public void inflate(JSONObject j) throws JSONException {
		setKey(j.getString("key"));
		setContent(j.getString("content"));
		setSecret(j.getString("secret"));
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
		cachedThumbnail = null;
	}
	
	public void setKind(int k) {
		kind = k;
		cachedThumbnail = null;
	}	
	
	public int getKind() {
		return kind;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public String getContent() {
		return content;
	}
	
	public boolean isNew() {
		return getRemoteID() == DatabaseObject.NEW_OBJECT;
	}
	
	static public Message getByID(int id) {
		try {
			return getDAO().queryForId(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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

	public String getKey() {
		return key;
	}
	
	public void refresh() {
			try {
				getDAO().refresh(this);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void setFilePath(File imageFile) {
		content = imageFile.getAbsolutePath();
		setKind(KIND_IMAGE_PATH);
	}
	
	private Bitmap cachedThumbnail = null;
	
	public Bitmap getThumbnailBitmap() {
		if(cachedThumbnail != null)
			return cachedThumbnail;
		
		if(kind == KIND_IMAGE)
		{
			byte[] decoded = Base64.decode( getContent(), Base64.DEFAULT );
			Bitmap bmp=BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
			cachedThumbnail = bmp;
		}
		else {			
            cachedThumbnail = Util.openBitmap(getContent(), true);            
		}
		
		return cachedThumbnail;
	}

	public String getImageRemotePath() {
		return String.format("/images?secret=%s", this.secret);
	}
}
