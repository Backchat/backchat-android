package com.youtell.backdoor.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Gab {
	public String getTitle() {
		if(isAnonymous()) {
			if(this.related_user_name == null || this.related_user_name.isEmpty())
				return "???";
		}
		
		return this.related_user_name;
	}
	
	private String related_user_name;
	private String related_avatar;
	private Date updated_at;
	
	private String id;
	private List<Message> messages;
	private boolean sent; //actually whether it is anon or not, e.g. "whether it was sent by us"
	
	public Gab(String id, String related_user_name, String text, Date updated_at, boolean sent) {
		this.related_user_name = related_user_name;
		this.id = id;
		this.messages = new ArrayList<Message>();
		this.sent = sent;
		this.updated_at = updated_at;
		this.messages.add(new Message(text, false, true));
		this.messages.add(new Message("Mine", true, true));
		this.messages.add(new Message("A longer message that is long", false, true));
		this.messages.add(new Message("A shorter message that is delivered", true, true));		
	}
	
	public String getID() {
		return id;
	}
	
	public List<Message> getMessages() {
		return messages;
	}

	public boolean isAnonymous() {
		return !sent;
	}

	public String getRelatedUserName() {
		return related_user_name;
	}
	
	public void setRelatedUserName(String relatedUserName) {
		related_user_name = relatedUserName;
	}

	public Date getUpdatedAt() {
		return updated_at;		
	}
}
