package com.youtell.backdoor.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.youtell.backdoor.BaseGabDetailActivity;
import com.youtell.backdoor.GabAnonymousDetailActivity;
import com.youtell.backdoor.GabDetailActivity;
import com.youtell.backdoor.GabDetailFragment;

public class Gab {	
	private String related_user_name;
	private String related_avatar;
	private Date updated_at;
	private String content_summary;
	
	private String id;
	private List<Message> messages;
	private boolean sent; //actually whether it is anon or not, e.g. "whether it was sent by us"
	
	public Gab()
	{	
		this.messages = new ArrayList<Message>();
	}
	
	public Gab(String id, String related_user_name, String text, Date updated_at, boolean sent) {
		this.related_user_name = related_user_name;
		this.id = id;
		this.messages = new ArrayList<Message>();
		this.sent = sent;
		this.updated_at = updated_at;
		this.content_summary = text;
		Date d = new Date();
		this.messages.add(new Message(text, false, d, true));
		this.messages.add(new Message("Mine", true, new Date(d.getTime() + 10*1000), true));
		this.messages.add(new Message("A longer message that is long", false, new Date(d.getTime() + 60*10*1000), true));
		this.messages.add(new Message("A shorter message that is delivered", true, new Date(d.getTime() + 60*10*1000+1), true));		
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

	public void setIsAnonymous(boolean anon) {
		sent = !anon;
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

	public void setID(String newGabID) {
		id = newGabID;		
	}

	public void setUpdatedAt(Date date) {
		updated_at = date;
	}

	public void startDetailIntent(Context context) {
		Class<? extends BaseGabDetailActivity> classType;
		if(isAnonymous())
			classType = GabAnonymousDetailActivity.class;
		else
			classType = GabDetailActivity.class;
    	Intent detailIntent = new Intent(context, classType);
    	detailIntent.putExtra(BaseGabDetailActivity.ARG_GAB_ID, getID());
    	context.startActivity(detailIntent);		
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

	public String getContentSummary() {
		return content_summary;
	}

	public void addMessage(Message m) {
		messages.add(m);
	}
	
}
