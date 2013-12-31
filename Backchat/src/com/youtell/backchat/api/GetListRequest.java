package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.youtell.backchat.models.DatabaseObject;
import com.youtell.backchat.models.User;

public abstract class GetListRequest<T extends DatabaseObject> extends GetRequest {
	private Class<T> clazz;
	
	protected GetListRequest(Class<T> clazz)
	{
		this.clazz = clazz;
	}
	
	protected void deleteObjects(List<Integer> remoteIDsTouched) throws JSONException {
		try {
			clazz.getMethod("removeByNotRemoteIDs", List.class).invoke(null, remoteIDsTouched);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new JSONException(e.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	@SuppressLint("UseValueOf")
	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		JSONArray items = getJSONItemArray(result);
		ArrayList<Integer> remoteIDsTouched = new ArrayList<Integer>();
		
		for(int i=0;i<items.length();i++) {
			JSONObject data = items.getJSONObject(i);
			int remoteID = data.getInt("id");
			T obj;
			try {
				obj = (T) clazz.getMethod("getByRemoteID", int.class).invoke(null, remoteID);
			
				if(obj == null) {
					obj = clazz.newInstance();
					obj.setRemoteID(remoteID);
				}
			} catch (Exception e) {
				throw new JSONException(e.toString());
			}

			obj.inflate(data);
			obj.save();		
			
			remoteIDsTouched.add(new Integer(remoteID));
		}
		
		deleteObjects(remoteIDsTouched);
	}

	protected abstract JSONArray getJSONItemArray(JSONObject result) throws JSONException;
}
