package com.youtell.backdoor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.youtell.backdoor.R;
import com.youtell.backdoor.models.Database;

public class LoginActivity extends BaseActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);		      
	}
	
	public void fbButtonClick(View v)
	{
		Intent intent = null;	
		Database.setDatabaseForUser(0);
		intent = new Intent(this, GabListActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void gppButtonClick(View v)
	{
		Intent intent = null;
		Database.setDatabaseForUser(1);
		intent = new Intent(this, GabListActivity.class);
		startActivity(intent);
		finish();
	}
}
