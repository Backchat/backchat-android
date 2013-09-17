package com.youtell.backdoor.api;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.Gab;

import android.os.Bundle;

public class GetGabsRequest extends GetListRequest<Gab> {	
	public GetGabsRequest() {
		super(Gab.class);
	}
	
	@Override
	public HttpUriRequest getRequestURI() {
		return new HttpGet("/gabs");
	}

	@Override
	protected void addArguments(Bundle b) {
	}

	@Override
	protected void inflateArguments(Bundle args) {		
	}

	@Override
	protected JSONArray getJSONItemArray(JSONObject result)
			throws JSONException {
		return result.getJSONArray("gabs");
	}

}
