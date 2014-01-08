package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.GabObserver;

import android.os.Bundle;

public class GetGabsRequest extends GetListRequest<Gab> {	
	public GetGabsRequest() {
		super(Gab.class);
	}

	@Override
	protected JSONArray getJSONItemArray(JSONObject result)
			throws JSONException {
		return result.getJSONArray("gabs");
	}

	@Override
	protected List<NameValuePair> getParameters() {
		return new ArrayList<NameValuePair>();
	}

	@Override
	protected String getPath() {
		return "/gabs";
	}

	
	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		super.handleJSONResponse(result, user);
		GabObserver.broadcastChange(GabObserver.GAB_BATCH_UPDATED, null);
	}

}
