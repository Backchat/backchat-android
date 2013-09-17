package com.youtell.backdoor.api;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

import com.youtell.backdoor.models.User;

import android.util.Log;

public abstract class GetRequest extends Request {
	@Override
	public void execute(HttpClient client, User user) {
		String result;
		List<NameValuePair> nameValuePairs = getParameters();
		nameValuePairs.add(new BasicNameValuePair("access_token", user.getApiToken()));
		BasicResponseHandler handler = new BasicResponseHandler();
		String queryString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		try {
			URI uri = new URI("http", user.getApiServerHostName(), getPath(), queryString, null); 
			HttpGet request = new HttpGet(uri);
			result = client.execute(request, handler);
		} catch (Exception e) {
			Log.e("APISERVER", "ERROR", e);
			return;
		}
		
		handleResult(result);
	}

}
