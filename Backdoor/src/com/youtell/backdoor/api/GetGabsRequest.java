package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
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

	@Override
	protected List<NameValuePair> getParameters() {
		return new ArrayList<NameValuePair>();
	}

	@Override
	protected String getPath() {
		return "/gabs";
	}

	@Override
	protected void handleServerFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleParsingFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleInternetFailure() {
		// TODO Auto-generated method stub
		
	}

}
