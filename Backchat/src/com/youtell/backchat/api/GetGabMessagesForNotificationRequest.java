package com.youtell.backchat.api;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.GCMNotificationObserver;

public class GetGabMessagesForNotificationRequest extends GetGabMessagesRequest {
	private static final String MESSAGE_ARG = "MESSAGE_ARG";
	private static final String GAB_ID_ARG = "GAB_ID_ARG";
	private StringArgumentHandler message = new StringArgumentHandler(MESSAGE_ARG, this);
	private IntegerArgumentHandler gabID = new IntegerArgumentHandler(GAB_ID_ARG, this);

	public GetGabMessagesForNotificationRequest() {
		super();
	}
	
	public GetGabMessagesForNotificationRequest(int gab_id, String message) {
		super();
		this.message.content = message;
		this.gabID.value = gab_id;
	}
	
	@Override
	@SuppressLint("DefaultLocale")
	protected String getPath() {
		return String.format("/gabs/%d", gabID.value);
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		Gab g = Gab.getByRemoteID(gabID.value);
		if(g == null) {
			g = new Gab();
			g.setRemoteID(gabID.value);
		}
		gab.setObject(g);
		super.handleJSONResponse(result, user);
	}
	
	@Override
	protected void handleSuccess()
	{
		super.handleSuccess();
		Gab g = Gab.getByRemoteID(gabID.value);
		
		GCMNotificationObserver.broadcastMessage(context, message.content, g);
	}

}
