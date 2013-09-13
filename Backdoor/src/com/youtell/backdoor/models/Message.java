package com.youtell.backdoor.models;

public class Message {
	public String text;
	private boolean sent; //acutally whether it was sent by us, e.g. isMine.
	private boolean state; //whether it is delivered or not.
	/* really, this is only client side. we set it to false, and when we get
	 * the ack back, we set it to true.
	 */
	
	public Message(String text, boolean sent, boolean state) {
		this.text = text;
		this.sent = sent;
		this.state = state;
	}
	
	public boolean isMine() {
		return sent;
	}
	
	public boolean isSent() {
		return state;
	}
}
