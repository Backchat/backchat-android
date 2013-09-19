package com.youtell.backdoor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.youtell.backdoor.R;
import com.youtell.backdoor.models.Database;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.UserObserver;

public class LoginActivity extends BaseActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);		      
	}
	
	public void fbButtonClick(View v)
	{
		User u = new User();
		u.setApiToken("CAAG82yXJNQgBAPmhHZALWVxHVKti5rtGHEcR7U9nMxZCUibyZCQyJYlSytMnmlyCdKZBOhZAsPAdIVG1dmJMcZADmZCuOdF2XncZBCBjTQVfA1ZB0Kw5R0elFMNgmfyeRuz1VVhTvPCzSdZCSsgmOITuWcyZAFxFRG4amd1ZBi3pihM7VAZDZD");
		u.setApiServerHostName("backdoor-stage.herokuapp.com");
		u.setGCMKey("412155847073");
		UserObserver.broadcastUserSwapped(u);
		u.setFullName("Lin Xu");
		u.setTotalClueCount(10);

		Intent intent = null;	
		intent = new Intent(this, GabListActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void gppButtonClick(View v)
	{
		return;
		/*
		Intent intent = null;
		intent = new Intent(this, GabListActivity.class);
		startActivity(intent);
		finish();*/
	}
}
