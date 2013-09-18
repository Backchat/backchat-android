package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.Util;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;

import android.annotation.SuppressLint;
import android.os.Bundle;

public class PostMessageRequest extends PostRequest {
	private TypedArgumentHandler<Gab> gab = new TypedArgumentHandler<Gab>(Gab.class, this);
	private TypedArgumentHandler<Message> message = new TypedArgumentHandler<Message>(Message.class, this);
	
	public PostMessageRequest() 
	{
	}
	
	public PostMessageRequest(Gab g, int messageID) 
	{
		gab.setObject(g);
		message.setObjectByID(messageID);
	}
	
	public PostMessageRequest(Gab g, Message m)
	{
		super();
		gab.setObject(g);
		message.setObject(m);
	}
	
	@Override
	protected void handleJSONResponse(JSONObject result) throws JSONException {
		JSONObject gabData = result.getJSONObject("gab");
		gab.object.inflate(gabData);
		gab.object.save();
		
		JSONObject messageData = result.getJSONObject("message");
		
		message.object.setRemoteID(messageData.getInt("id"));
		message.object.inflate(messageData);
		message.object.save();
	}

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("content", message.object.getContent()));
        nameValuePairs.add(new BasicNameValuePair("kind", Integer.valueOf(message.object.getKind()).toString()));
        message.object.setKey(Util.generatePseudoRandomString(16)); //TODO move?
        nameValuePairs.add(new BasicNameValuePair("key", message.object.getKey()));
        return nameValuePairs;
	}

	@SuppressLint("DefaultLocale")
	@Override
	protected String getPath() {
		return String.format("/gabs/%d/messages", gab.object.getRemoteID());
	}


}
