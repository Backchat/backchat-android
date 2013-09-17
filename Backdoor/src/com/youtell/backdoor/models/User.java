package com.youtell.backdoor.models;


import com.youtell.backdoor.api.GetFriendsRequest;
import com.youtell.backdoor.api.GetGabsRequest;
import com.youtell.backdoor.services.APIService;

public class User {

	public void updateGabs() {
		APIService.fire(new GetGabsRequest());
	}

	public String getApiToken() {
		return "CAAG82yXJNQgBAPmhHZALWVxHVKti5rtGHEcR7U9nMxZCUibyZCQyJYlSytMnmlyCdKZBOhZAsPAdIVG1dmJMcZADmZCuOdF2XncZBCBjTQVfA1ZB0Kw5R0elFMNgmfyeRuz1VVhTvPCzSdZCSsgmOITuWcyZAFxFRG4amd1ZBi3pihM7VAZDZD";
	}

	public void getFriends() {
		APIService.fire(new GetFriendsRequest());
	}

	public String getApiServerHostName() {
		return "backdoor-stage.herokuapp.com";
	}
}
