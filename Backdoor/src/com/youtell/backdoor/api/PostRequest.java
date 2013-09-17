package com.youtell.backdoor.api;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.youtell.backdoor.models.User;

public abstract class PostRequest extends Request {
	@Override
	public void execute(HttpClient client, User user) {
		String result;
		List<NameValuePair> nameValuePairs = getParameters();
		nameValuePairs.add(new BasicNameValuePair("access_token", user.getApiToken()));
		BasicResponseHandler handler = new BasicResponseHandler();
		try {
			URI uri = new URI("http", user.getApiServerHostName(), getPath(), null, null); 
			HttpPost request = new HttpPost(uri);
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			result = client.execute(request, handler);
		} catch (Exception e) {
			Log.e("APISERVER", "ERROR", e);
			return;
		}
		
		handleResult(result);
	}

}
