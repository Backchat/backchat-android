package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.iap.PurchasedItem;
import com.youtell.backchat.models.User;

import android.os.Bundle;

public class PostFreeShareClueRequest extends PostRequest {
	public PostFreeShareClueRequest() {}
	
	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("reason", "freeclues"));
		return list;
	}
	
	@Override
	protected String getPath() {
		return "/free-clues";
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		int total_available = result.getInt("available_clues");
		User.getCurrentUser().updateTotalClues(total_available);
	}

}
