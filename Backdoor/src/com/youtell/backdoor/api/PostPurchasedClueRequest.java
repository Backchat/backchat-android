package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.iap.PurchasedItem;
import com.youtell.backdoor.models.User;

import android.os.Bundle;

public class PostPurchasedClueRequest extends PostRequest {
	private InflatableArgumentHandler<PurchasedItem> item = new InflatableArgumentHandler<PurchasedItem>(PurchasedItem.class, this);
	
	public PostPurchasedClueRequest() {}
	public PostPurchasedClueRequest(PurchasedItem item) {
		this.item.setObject(item);
	}
	
	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("receiptgoogle", item.object.getPurchaseData()));
		return list;
	}
	
	@Override
	protected String getPath() {
		return "/buy-clues";
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		int total_available = result.getInt("available_clues");
		User.getCurrentUser().updateTotalClues(total_available); //TODO should be broadcuast
	}

}
