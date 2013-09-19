package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;
import com.youtell.backdoor.fragments.NewGabFragment;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;

import android.app.ActionBar;
import android.os.Bundle;

public class NewGabActivity extends BaseActivity implements NewGabFragment.Callbacks {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_gab);
		setupTitleActionBar();	     
	}

	@Override
	public void onItemSelected(Friend f) {
		Gab gab = f.createNewGab();
		gab.save();
		startActivity(BaseGabDetailActivity.getDetailIntent(this, gab));
	}
}
