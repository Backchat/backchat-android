package com.youtell.backdoor.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.observers.GabObserver;

import android.os.Bundle;
import android.os.SystemClock;

public class PostGabRequest extends PostRequest {
	private Gab gab;
	
	private static final String ARG_GAB_ID = "ARG_GAB_ID";
	
	public PostGabRequest() 
	{		
	}
	
	public PostGabRequest(Gab g) {
		gab = g;
	}
	
	public PostGabRequest(int gabID) {
		gab = Gab.getByID(gabID);
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
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Message m = gab.getFirstMessage();
		
		params.add(new BasicNameValuePair("message[content]", m.getContent()));
		params.add(new BasicNameValuePair("message[kind]", Integer.toString(m.getKind())));
		params.add(new BasicNameValuePair("message[key]", m.getKey()));
		
		Friend f = gab.getRelatedFriend();
		params.add(new BasicNameValuePair("friendship[id]", Integer.toString(f.getRemoteID())));
		
		return params;
	}

	@Override
	protected String getPath() {
		return "/gabs";
	}

	@Override
	protected void handleJSONResponse(JSONObject result) throws JSONException {
		SystemClock.sleep(1000);
		JSONObject gabPart = result.getJSONObject("gab");
		gab.setRemoteID(gabPart.getInt("id"));
		gab.inflate(gabPart);
		gab.save();

		JSONArray messagePart = gabPart.getJSONArray("messages");
		JSONObject msgData = messagePart.getJSONObject(0);
		int remoteID = msgData.getInt("id");
		Message m;
		m = gab.getFirstMessage();
		m.setRemoteID(remoteID);
		m.inflate(msgData);
		m.save();
		
		GabObserver.broadcastChange(GabObserver.GAB_INSERTED, gab);
	}
}
