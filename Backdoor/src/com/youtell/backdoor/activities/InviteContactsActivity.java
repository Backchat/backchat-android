package com.youtell.backdoor.activities;

import android.app.ActionBar;
import android.os.Bundle;

import com.youtell.backdoor.R;
import com.youtell.backdoor.fragments.InviteContactsFragment;

public class InviteContactsActivity extends BaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_contacts);
		
		InviteContactsFragment fragment = new InviteContactsFragment();
		getFragmentManager().beginTransaction()
		.add(R.id.invite_contacts_container, fragment)
		.commit();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}
}
