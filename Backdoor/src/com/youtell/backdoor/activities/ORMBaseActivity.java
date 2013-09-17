package com.youtell.backdoor.activities;

import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.youtell.backdoor.models.Database;

public class ORMBaseActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OpenHelperManager.getHelper(this, Database.class);
	}	

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		OpenHelperManager.releaseHelper();
	}
}
