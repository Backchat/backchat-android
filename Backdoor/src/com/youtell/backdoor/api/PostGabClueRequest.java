package com.youtell.backdoor.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.youtell.backdoor.models.Clue;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.ClueObserver;

public class PostGabClueRequest extends PostRequest {
	private TypedArgumentHandler<Clue> clue = new TypedArgumentHandler<Clue>(Clue.class, this);

	public PostGabClueRequest() {}
	
	public PostGabClueRequest(Clue clue) {
		this.clue.setObject(clue);
	}

	@Override
	protected List<NameValuePair> getParameters() {
		return new ArrayList<NameValuePair>();
	}

	@Override
	protected String getPath() {
		return String.format("/gabs/%d/clues/request/%d", clue.object.getGab().getRemoteID(), clue.object.getNumber());
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		Log.e("postgabclues", result.toString());
		int availableClues = result.getInt("available_clues"); 
		User.getCurrentUser().updateTotalClues(availableClues); //TODO should be broadcast
		JSONObject clueData = result.getJSONObject("clue");
		int remoteID = clueData.getInt("id");
		clue.object.setRemoteID(remoteID);
		clue.object.inflate(clueData);
		clue.object.save();
	}

}
