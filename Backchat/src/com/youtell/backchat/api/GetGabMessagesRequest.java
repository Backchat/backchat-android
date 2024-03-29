package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.Message;
import com.youtell.backchat.models.User;

import android.annotation.SuppressLint;

public class GetGabMessagesRequest extends GetRequest {
	protected TypedArgumentHandler<Gab> gab = new TypedArgumentHandler(Gab.class, this);
	
	public GetGabMessagesRequest() {			
	}
	
	public GetGabMessagesRequest(Gab g) {
		gab.setObject(g);
	}

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		p.add(new BasicNameValuePair("extended", "true"));
		return p;
	}

	@Override
	@SuppressLint("DefaultLocale")
	protected String getPath() {
		return String.format("/gabs/%d", gab.object.getRemoteID());
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		JSONObject gabPart = result.getJSONObject("gab");		
		gab.object.inflate(gabPart);
		gab.object.save();
		gab.object.refresh(); //in case this was the first save we need to refresh to get the messages object back.

		JSONArray messagePart = gabPart.getJSONArray("messages");
			
		for(int i=0;i<messagePart.length();i++) { 
			JSONObject msgData = messagePart.getJSONObject(i);
			int remoteID = msgData.getInt("id");
			Message m;
			m = gab.object.getMessageByRemoteID(remoteID);
					
			if(m == null) {
				m = new Message();
				m.setRemoteID(remoteID);
				m.inflate(msgData);
				gab.object.addMessage(m);
			}
			else {
				m.inflate(msgData);
				m.save();
			}
		}
	}
		
}
