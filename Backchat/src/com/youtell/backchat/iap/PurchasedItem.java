package com.youtell.backchat.iap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.youtell.backchat.models.InflatableObject;

public class PurchasedItem implements InflatableObject {
	private String sku;
	private String token;
	private String purchaseData;
	
	private static final String SKU_ARG = "SKU_ARG";
	private static final String TOKEN_ARG = "TOKEN_ARG";
	private static final String PURCHASE_ARG = "PURCHASE_ARG";
	
	public String getSKU() {
		return sku;
	}
	
	public String getToken() {
		return token;
	}
	
	public PurchasedItem() {}
	
	public PurchasedItem(String sku, String token) {
		this.sku = sku;
		this.token = token;
	}
	
	public String getPurchaseData() {
		return purchaseData;
	}
	
	public PurchasedItem(String purchaseData) throws JSONException {
		JSONObject data = null;
		Log.e("IAP", purchaseData);
		data = new JSONObject(purchaseData);
		token = data.getString("purchaseToken");
		sku = data.getString("productId");
		this.purchaseData = purchaseData;
	}
	
	@Override
	public void serialize(Bundle b) {
		b.putString(PURCHASE_ARG, purchaseData);
		b.putString(TOKEN_ARG, token);
		b.putString(SKU_ARG, sku);
	}
	
	@Override
	public void deserialize(Bundle b) {
		purchaseData = b.getString(PURCHASE_ARG);
		token = b.getString(TOKEN_ARG);
		sku = b.getString(SKU_ARG);
	}
}
