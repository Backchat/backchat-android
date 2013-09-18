package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public class DeleteGabRequest extends DeleteRequest {
	private int remoteID;
	
	private static final String ARG_REMOTE_ID = "ARG_REMOTE_ID";
	
	public DeleteGabRequest() {}
	public DeleteGabRequest(int remoteID) {
		this.remoteID = remoteID;
	}
	
	@Override
	protected void addArguments(Bundle b) {
		b.putInt(ARG_REMOTE_ID, remoteID);
	}

	@Override
	protected void inflateArguments(Bundle args) {
		remoteID = args.getInt(ARG_REMOTE_ID);
	}

	@Override
	protected List<NameValuePair> getParameters() {
		return new ArrayList<NameValuePair>();
	}

	@Override
	protected String getPath() {
		return String.format("/gabs/%d", remoteID);
	}

	@Override
	protected void handleJSONResponse(JSONObject result) throws JSONException {
		//???
	}


}
