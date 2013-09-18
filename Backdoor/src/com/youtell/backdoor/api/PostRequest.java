package com.youtell.backdoor.api;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import com.youtell.backdoor.models.User;

public abstract class PostRequest extends Request {
	@Override
	protected HttpUriRequest getRequest(User user) throws Exception {

		List<NameValuePair> nameValuePairs = getParameters();
		nameValuePairs.add(new BasicNameValuePair("access_token", user.getApiToken()));
		URI uri = new URI("http", user.getApiServerHostName(), getPath(), null, null); 
		HttpPost request = new HttpPost(uri);
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		return request;
	}

}
