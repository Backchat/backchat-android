package com.youtell.backchat.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.youtell.backchat.Application;
import com.youtell.backchat.fragments.InviteContactsFragment;
import com.youtell.backchat.R;

public class InviteContactsActivity extends BaseActivity implements InviteContactsFragment.Callbacks {
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
		JSONObject props = new JSONObject();
		try {
			props.put("count", contactIds.size());
			Application.mixpanel.track("Tapped Invite Contact View / Invite", props);
		} catch (JSONException e) {
			e.printStackTrace();
		}

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
			 Application.mixpanel.track("Tapped Invite Contact View / select all", null);
			 inviteContacts.selectAll();
			 return true;
		 }
		 else if(item.getItemId() == android.R.id.home) {
			 Application.mixpanel.track("Tapped Invite Contact View / Cancel", null);
			 goUp();
			 return true;
		 }
		 
		 return false;
	 }
}