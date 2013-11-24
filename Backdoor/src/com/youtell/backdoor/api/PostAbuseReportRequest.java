package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backdoor.models.User;
import com.youtell.backdoor.services.APIService;

public class PostAbuseReportRequest extends PostRequest {
	private StringArgumentHandler message = new StringArgumentHandler("message", this);
	
	public PostAbuseReportRequest() {}

	public PostAbuseReportRequest(String message) {
		super();
		this.message.content = message;
	}
	
	@Override
	protected List<NameValuePair> getParameters() {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("content", message.content));
		return args;
	}

	@Override
	protected String getPath() {
		return "/report-abuse";
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user)
			throws JSONException {
		//always good.
		APIService.mixpanel.track("Sent Abuse Report", null);
	}

}
