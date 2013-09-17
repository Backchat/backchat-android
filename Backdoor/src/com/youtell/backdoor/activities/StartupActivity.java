package com.youtell.backdoor.activities;

import android.content.Intent;
import android.os.Bundle;

public class StartupActivity extends android.app.Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = null;
		if(false) {
			intent = new Intent(this, GabListActivity.class);
		}
		else {
			intent = new Intent(this, LoginActivity.class);
		}
		
		startActivity(intent);
		overridePendingTransition(0,0);	
		finish();
	}
}
