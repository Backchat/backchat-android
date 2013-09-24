package com.youtell.backdoor.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.DatabaseObject;
import com.youtell.backdoor.models.User;

public abstract class GetListRequest<T extends DatabaseObject> extends GetRequest {
	private Class<T> clazz;
	
	protected GetListRequest(Class<T> clazz)
	{
		this.clazz = clazz;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		JSONArray items = getJSONItemArray(result);
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
		}
	}

	protected abstract JSONArray getJSONItemArray(JSONObject result) throws JSONException;
}
