package com.youtell.backchat.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtell.backchat.models.Clue;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;

public class GetGabCluesRequest extends GetRequest {
	private TypedArgumentHandler<Gab> gab = new TypedArgumentHandler<Gab>(Gab.class, this);

	GetGabCluesRequest() {
		super();
	}
	
	public GetGabCluesRequest(Gab g) {
		super();
		gab.setObject(g);
	}
	
	@Override
	protected List<NameValuePair> getParameters() {
		return new ArrayList<NameValuePair>();
	}

	@Override
	protected String getPath() {
		return String.format("/gabs/%d/clues", gab.object.getRemoteID());
	}

	@Override
	protected void handleJSONResponse(JSONObject result, User user) throws JSONException {
		JSONArray clues = result.getJSONArray("clues");
		User.getCurrentUser().updateTotalClues(result.getInt("available_clues")); //TODO this should be broadcast not assumed 
		
		for(int i=0;i<clues.length();i++) {
			JSONObject clue = clues.getJSONObject(i);
			int remoteID = clue.getInt("id");
			Clue c;
			c = gab.object.getClueByRemoteID(remoteID);
					
			if(c == null) {
				c = new Clue();
				c.setRemoteID(remoteID);
				c.inflate(clue);
				gab.object.addClue(c);
			}
			else {
				c.inflate(clue);
				c.save();
			}
		}
	}
	

}
