package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.youtell.backchat.models.User;
import com.youtell.backchat.services.APIService;

public class PostUserDataRequest extends PostRequest {
	public static final String FacebookData = "fb_data";
	public static final String GPPData = "gpp_data";

	private StringArgumentHandler userData = new StringArgumentHandler("userData", this);
	//TODO what if this is bigger then 1kb?
	private StringArgumentHandler dataType = new StringArgumentHandler("dataType", this);

	public PostUserDataRequest() {
		super();
	}

	public PostUserDataRequest(String dataType, JSONObject data) {
		this.dataType.content = dataType;
		userData.content = data.toString();
		Log.e("PostUserData", userData.content);		
	}

	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair(dataType.content, userData.content));
		return args;
	}

	@Override
	protected String getPath() {
		return "/";
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user)
			throws JSONException {
		//assume good
	}

}
