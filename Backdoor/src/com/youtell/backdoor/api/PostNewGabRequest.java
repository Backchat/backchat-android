package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.GabObserver;

public class PostNewGabRequest extends PostRequest {
	private TypedArgumentHandler<Gab> gab = new TypedArgumentHandler<Gab>(Gab.class, this);
		
	public PostNewGabRequest() 
	{		
	}
	
	
	public PostNewGabRequest(Gab g) {
		gab.setObject(g);
	}
	
	public PostNewGabRequest(int gabID) {
		gab.setObjectByID(gabID);
	}
	

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Message m = gab.object.getFirstMessage();
		
		params.add(new BasicNameValuePair("message[content]", m.getContent()));
		params.add(new BasicNameValuePair("message[kind]", Integer.toString(m.getKind())));
		params.add(new BasicNameValuePair("message[key]", m.getKey()));
		
		Friend f = gab.object.getRelatedFriend();
		params.add(new BasicNameValuePair("friendship[id]", Integer.toString(f.getRemoteID())));
		
		return params;
	}

	@Override
	protected String getPath() {
		return "/gabs";
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		JSONObject gabPart = result.getJSONObject("gab");
		gab.object.setRemoteID(gabPart.getInt("id"));
		gab.object.inflate(gabPart);
		gab.object.save();

		JSONArray messagePart = gabPart.getJSONArray("messages");
		JSONObject msgData = messagePart.getJSONObject(0);
		int remoteID = msgData.getInt("id");
		Message m;
		m = gab.object.getFirstMessage();
		m.setRemoteID(remoteID);
		m.inflate(msgData);
		m.save();
		
		GabObserver.broadcastChange(GabObserver.GAB_INSERTED, gab.object);
	}
}
