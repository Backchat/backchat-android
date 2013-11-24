package com.youtell.backdoor.api;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.Util;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.services.APIService;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Base64;

public class PostMessageRequest extends PostRequest {
	private TypedArgumentHandler<Gab> gab = new TypedArgumentHandler<Gab>(Gab.class, this);
	private TypedArgumentHandler<Message> message = new TypedArgumentHandler<Message>(Message.class, this);
	
	public PostMessageRequest() 
	{
	}
	
	public PostMessageRequest(Gab g, int messageID) 
	{
		gab.setObject(g);
		message.setObjectByID(messageID);
	}
	
	public PostMessageRequest(Gab g, Message m)
	{
		super();
		gab.setObject(g);
		message.setObject(m);
	}
	
	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		JSONObject gabData = result.getJSONObject("gab");
		gab.object.inflate(gabData);
		gab.object.save();
		
		JSONObject messageData = result.getJSONObject("message");
		
		message.object.setRemoteID(messageData.getInt("id"));
		message.object.inflate(messageData);
		message.object.save();
		
		JSONObject prop = new JSONObject();
		prop.put("Anonymous", gab.object.isAnonymous());
		APIService.mixpanel.track("Sent Message", prop);
	}

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		String content;
		String kind;
		if(message.object.getKind() == Message.KIND_TEXT) {
			content = message.object.getContent();
			kind = "0";
		}
		else {
			//cloned from iOS app, JPEG @ 0.85
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			Bitmap b = Util.openBitmap(message.object.getContent(),  false);
			b.compress(CompressFormat.JPEG, 85, byteStream);
			byte[] array = byteStream.toByteArray();
			String asBase64 = Base64.encodeToString(array, Base64.DEFAULT);
			b.recycle();
			content = asBase64;
			kind = "1";
		}
        nameValuePairs.add(new BasicNameValuePair("content", content));
        nameValuePairs.add(new BasicNameValuePair("kind", kind));
        message.object.setKey(Util.generatePseudoRandomString(16)); //TODO move?
        nameValuePairs.add(new BasicNameValuePair("key", message.object.getKey()));
        return nameValuePairs;
	}

	@SuppressLint("DefaultLocale")
	@Override
	protected String getPath() {
		return String.format("/gabs/%d/messages", gab.object.getRemoteID());
	}


}
