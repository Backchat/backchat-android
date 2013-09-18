package com.youtell.backdoor.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.youtell.backdoor.R;
import com.youtell.backdoor.fragments.InviteContactsFragment;

public class InviteContactsActivity extends ORMBaseActivity implements InviteContactsFragment.Callbacks {
	private InviteContactsFragment inviteContacts;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_contacts);
		setupTitleActionBar();

		inviteContacts = new InviteContactsFragment();
		getFragmentManager().beginTransaction()
		.add(R.id.invite_contacts_container, inviteContacts)
		.commit();
	}

	@Override
	public void onInvite(ArrayList<Integer> contactIds) {
		Intent intent = new Intent(this, InviteComposeActivity.class);
		intent.putIntegerArrayListExtra(InviteComposeActivity.ARG_CONTACT_IDS, contactIds);
		startActivity(intent);
	}

	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     getMenuInflater().inflate(R.menu.invite_contacts_actionbar, menu);
	     return true;
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 if(item.getItemId() == R.id.invite_contacts_select_all) {
			 inviteContacts.selectAll();
			 return true;
		 }
		 else if(item.getItemId() == android.R.id.home) {
			 goUp();
			 return true;
		 }
		 
		 return false;
	 }
}