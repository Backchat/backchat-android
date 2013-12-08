package com.youtell.backchat.iap;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	private String sku;
	public String description;
	public String price;
	public String title;
	
	public String getSKU() {
		return sku;
	}
	
	public Item(JSONObject object) throws JSONException {
		sku = object.getString("productId");
		price = object.getString("price");
		title = object.getString("title");
		description = object.getString("description");
	}
}
