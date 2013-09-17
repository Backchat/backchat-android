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
	private Gab gab;
	private Message message;
	
	private static final String ARG_GAB_ID = "ARG_GAB_ID";
	private static final String ARG_MESSAGE_ID = "ARG_MESSAGE_ID";
	
	public PostMessageRequest() 
	{
	}	
	
	public PostMessageRequest(Gab g, int messageID) 
	{
		super();
		gab = g;
		message = gab.getMessageByID(messageID);
	}
	
	public PostMessageRequest(Gab g, Message m)
	{
		super();
		gab = g;
		message = m;
	}
	
	@Override
	protected void addArguments(Bundle b) {
		b.putInt(ARG_GAB_ID, gab.getID());
		b.putInt(ARG_MESSAGE_ID, message.getID());
	}

	@Override
	protected void inflateArguments(Bundle args) {
		gab = Gab.getByID(args.getInt(ARG_GAB_ID));
		message = gab.getMessageByID(args.getInt(ARG_MESSAGE_ID));		
	}

	@Override
	protected void handleJSONResponse(JSONObject result) throws JSONException {
		JSONObject gabData = result.getJSONObject("gab");
		gab.inflate(gabData);
		gab.save();
		
		JSONObject messageData = result.getJSONObject("message");
		
		message.setRemoteID(messageData.getInt("id"));
		message.inflate(messageData);
		message.save();
	}

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("content", message.getContent()));
        nameValuePairs.add(new BasicNameValuePair("kind", Integer.valueOf(message.getKind()).toString()));
        message.setKey(Util.generatePseudoRandomString(16)); //TODO move?
        nameValuePairs.add(new BasicNameValuePair("key", message.getKey()));
        return nameValuePairs;
	}

	@SuppressLint("DefaultLocale")
	@Override
	protected String getPath() {
		return String.format("/gabs/%d/messages", gab.getRemoteID());
	}


}
