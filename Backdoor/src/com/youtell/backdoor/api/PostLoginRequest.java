package com.youtell.backdoor.api;

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

import com.youtell.backdoor.gcm.GCM;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.UserObserver;

import android.os.Bundle;

public class PostLoginRequest extends Request {
	private static final String TOKEN_ARG = "TOKEN_ARG";
	private static final String PROVIDER_ARG = "PROVIDER_ARG";
	private static final String HOSTNAME_ARG = "HOSTNAME_ARG";
	
	private String token;
	private String provider;
	private String hostName;
	
	public PostLoginRequest() {}
	public PostLoginRequest(String token, String provider, String hostName) {
		this.token = token;
		this.provider = provider;
		this.hostName = hostName;
	}
	
	@Override
	protected void inflateArguments(Bundle args) {
		this.token = args.getString(TOKEN_ARG);
		this.provider = args.getString(PROVIDER_ARG);
		this.hostName = args.getString(HOSTNAME_ARG);
	}
	
	@Override
	protected void addArguments(Bundle b) {
		b.putString(TOKEN_ARG, this.token);
		b.putString(PROVIDER_ARG, this.provider);
		b.putString(HOSTNAME_ARG, this.hostName);
	}
	
	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("access_token", token));
		pairs.add(new BasicNameValuePair("provider", provider));
		return pairs;
	}

	@Override
	protected String getPath() {
		return "/login";
	}

	@Override
	protected HttpUriRequest getRequest(User user) throws Exception {
		List<NameValuePair> nameValuePairs = getParameters();
		URI uri = new URI("http", hostName, getPath(), null, null); 
		HttpPost request = new HttpPost(uri);
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		return request;
	}
	
	@Override
	protected void handleJSONResponse(JSONObject result, User unused) throws JSONException {
		/* user is null! */
		/* parse out the stuff into a user */
		User user = new User();
		user.setApiServerHostName(hostName);
		user.setApiToken(token);
		JSONObject userData = result.getJSONObject("user");
		user.setID(userData.getInt("id"));
		user.setTotalClueCount(userData.getInt("available_clues"));
		user.setFullName(userData.getString("full_name"));
		user.setGCMKey(GCM.GCM_KEY); //TODO dynamic
		
		UserObserver.broadcastUserSwapped(user);
	}

}
