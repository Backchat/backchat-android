package com.youtell.backchat.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.Settings;
import com.youtell.backchat.gcm.GCM;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.UserObserver;
import com.youtell.backchat.social.SocialProvider;

import android.os.Bundle;

public class PostLoginRequest extends Request {
	private StringArgumentHandler token = new StringArgumentHandler("token", this);
	private StringArgumentHandler provider = new StringArgumentHandler("provider", this);
	private StringArgumentHandler hostName = new StringArgumentHandler("hostname", this);
	
	public PostLoginRequest() {}
	public PostLoginRequest(SocialProvider provider, String hostName) {
		this.token.content = provider.getToken();
		this.provider.content = provider.getProviderName();
		this.hostName.content = hostName;
	}	
	
	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("access_token", token.content));
		pairs.add(new BasicNameValuePair("provider", provider.content));
		return pairs;
	}

	@Override
	protected String getPath() {
		return "";
	}

	@Override
	protected HttpUriRequest getRequest(User user) throws Exception {
		List<NameValuePair> nameValuePairs = getParameters();
		URI uri = new URI("https", hostName.content, "", null, null); 
		HttpPost request = new HttpPost(uri);
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		return request;
	}
	
	@Override
	protected void handleJSONResponse(JSONObject result, User unused) throws JSONException {
		/* user is null! */
		/* parse out the stuff into a user */
		User user = new User();
		user.setApiServerHostName(Settings.settings.apiServerName); //TODO
		user.setApiToken(token.content);
		JSONObject userData = result.getJSONObject("user");
		user.setID(userData.getInt("id"));
		user.setTotalClueCount(userData.getInt("available_clues"));
		user.setFullName(userData.getString("full_name"));
		user.setIsNewUser(userData.getBoolean("new_user"));
		User.setCurrentUser(user);
	}

}
