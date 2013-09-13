package com.youtell.backdoor.models;

public class Message {
	public String text;
	
	public Message(String text) {
		this.text = text;
	}
	
	public boolean isMine() {
		return text == "Mine";
	}
}
