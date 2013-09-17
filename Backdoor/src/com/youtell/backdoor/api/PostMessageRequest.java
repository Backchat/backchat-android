package com.youtell.backdoor.api;

import org.apache.http.client.methods.HttpUriRequest;

import com.youtell.backdoor.models.Message;

import android.os.Bundle;

public abstract class PostMessageRequest extends Request {
	public PostMessageRequest(Message m)
	{
		super();
	}
	
	@Override
	protected void addArguments(Bundle b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void inflateArguments(Bundle args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HttpUriRequest getRequestURI() {
		// TODO Auto-generated method stub
		return null;
	}


}
