package com.youtell.backdoor.api;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;

import android.os.Bundle;

public class GetGabMessagesRequest extends Request {

	private static String ARG_GAB_ID = "ARG_GAB_ID";
	private Gab gab;
	
	public GetGabMessagesRequest() {		
	}
	
	public GetGabMessagesRequest(Gab g) {
		gab = g;
	}
	
	@Override
	protected void addArguments(Bundle b) {
		b.putInt(ARG_GAB_ID, gab.getID());
	}

	@Override
	protected void inflateArguments(Bundle args) {
		gab = Gab.getByID(args.getInt(ARG_GAB_ID));
	}

	@Override
	public HttpUriRequest getRequestURI() {
		return new HttpGet(String.format("/gabs/%d?extended=true", gab.getRemoteID()));
	}

	@Override
	protected void handleJSONResponse(JSONObject result) throws JSONException {
		JSONObject gabPart = result.getJSONObject("gab");
		JSONArray messagePart = gabPart.getJSONArray("messages");
		gab.inflate(gabPart);
		gab.save();
		
		for(int i=0;i<messagePart.length();i++) { 
			JSONObject msgData = messagePart.getJSONObject(i);
			int remoteID = msgData.getInt("id");
			Message m;
			m = gab.getMessageByRemoteID(remoteID);
			
			if(m == null) {
				m = new Message();
				m.setRemoteID(remoteID);
				m.inflate(msgData);
				gab.addMessage(m);
			}
			else {
				m.inflate(msgData);
				m.save();
			}
		}
	}

}
