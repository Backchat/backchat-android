package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.Gab;

import android.os.Bundle;

public class PostTagGabRequest extends PostRequest {
	private TypedArgumentHandler<Gab> gab = new TypedArgumentHandler<Gab>(Gab.class, this);

	public PostTagGabRequest()
	{
	}
	
	public PostTagGabRequest(Gab g) {
		gab.setObject(g);
	}	

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("related_user_name", gab.object.getRelatedUserName()));
		return args;
	}

	@Override
	protected String getPath() {
		return String.format("/gabs/%d", gab.object.getRemoteID());
	}

	@Override
	protected void handleJSONResponse(JSONObject result) throws JSONException {
		JSONObject gabData = result.getJSONObject("gab");
		gab.object.inflate(gabData);
		gab.object.save();
	}

}
