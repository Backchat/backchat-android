package com.youtell.backdoor.models;

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
}
