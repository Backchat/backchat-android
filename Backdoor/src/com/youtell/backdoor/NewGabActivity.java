package com.youtell.backdoor;

import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;

import android.app.ActionBar;
import android.os.Bundle;

public class NewGabActivity extends BaseActivity implements NewGabFragment.Callbacks {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_gab);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		      
	}

	@Override
	public void onItemSelected(Friend f) {
		Gab gab = f.createNewGab();
		gab.startDetailIntent(this);
	}
}