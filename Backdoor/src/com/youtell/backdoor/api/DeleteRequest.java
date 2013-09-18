package com.youtell.backdoor.api;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.youtell.backdoor.models.User;

abstract public class DeleteRequest extends Request {
	@Override
	protected HttpUriRequest getRequest(User user) throws Exception {
		List<NameValuePair> nameValuePairs = getParameters();
		nameValuePairs.add(new BasicNameValuePair("access_token", user.getApiToken()));
		String queryString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		URI uri = new URI("http", user.getApiServerHostName(), getPath(), queryString, null); 
		HttpDelete request = new HttpDelete(uri);
		
		return request;
	}


}
