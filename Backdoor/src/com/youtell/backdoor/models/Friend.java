package com.youtell.backdoor.models;

import java.util.Date;

import com.youtell.backdoor.dummy.DummyContent;

public class Friend {
	private String id;
	private String first_name;
	private String last_name;
	
	public Friend(String id, String first_name, String last_name)
	{
		this.first_name = first_name;
		this.last_name = last_name;
		this.id = id;		
	}

	public String getID() { 
		return id;
	}

	public String getFullName() {
		return String.format("%s %s", this.first_name, this.last_name);
	}

	public Gab createNewGab() {
		Gab g = new Gab();
		g.setID(DummyContent.getNewGabID());
		g.setRelatedUserName(getFullName());
		g.setUpdatedAt(new Date());
		g.setIsAnonymous(false);
		DummyContent.addGab(g);
		return g;
	}
}
