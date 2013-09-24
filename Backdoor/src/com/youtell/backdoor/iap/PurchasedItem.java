package com.youtell.backdoor.iap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PurchasedItem {
	private String sku;
	private String token;
	
	public String getSKU() {
		return sku;
	}
	
	public String getToken() {
		return token;
	}
	
	public PurchasedItem(String sku, String token) {
	this.sku = sku;
	this.token = token;
	}
	
	public PurchasedItem(String purchaseData) throws JSONException {
		JSONObject data = null;
		Log.e("IAP", purchaseData);
		data = new JSONObject(purchaseData);
		token = data.getString("purchaseToken");
		sku = data.getString("productId");
	}
}
