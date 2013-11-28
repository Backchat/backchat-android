package com.youtell.backdoor.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;

import com.youtell.backdoor.Application;
import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostInviteRequest;
import com.youtell.backdoor.fragments.InviteComposeFragment;
import com.youtell.backdoor.observers.APIRequestObserver;
import com.youtell.backdoor.observers.GCMToastNotificationObserver;

public class InviteComposeActivity extends BaseActivity implements InviteComposeFragment.Callbacks,
APIRequestObserver.Observer<PostInviteRequest> {

	public static final String ARG_CONTACT_IDS = "ARG_CONTACT_IDS";

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_compose);
		setupTitleActionBar();

		ArrayList<Integer> contactIDs = getIntent().getIntegerArrayListExtra(ARG_CONTACT_IDS);

		inviteFragment = new InviteComposeFragment();
		Bundle args = new Bundle();
		args.putIntegerArrayList(InviteComposeFragment.ARG_CONTACT_IDS, contactIDs);
		inviteFragment.setArguments(args);
		getFragmentManager().beginTransaction()
		.add(R.id.invite_compose_container, inviteFragment)
		.commit();

		observer = new APIRequestObserver<PostInviteRequest>(this, PostInviteRequest.class);
	}

	private ProgressDialog dialog;
	private APIRequestObserver<PostInviteRequest> observer;
	private InviteComposeFragment inviteFragment;

	@Override 
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void afterSend() {
		dialog = ProgressDialog.show(this, 
				getResources().getText(R.string.invite_compose_sending_invite_dialog_title),
				getResources().getText(R.string.invite_compose_sending_invite_dialog_body), 
				true, false);
		observer.startListening();
	}

	private void goBackToGabList() {
		Intent gabListIntent = new Intent(this, GabListActivity.class);
		gabListIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	@Override
	public void onStop()
	{
		observer.stopListening();
		
		if(dialog != null) 
			dialog.dismiss();

		super.onStop();
	}

	@Override
	public void onSuccess() {
		closeProgressDialog();
		
		Application.mixpanel.track("Tapped Compose Invite View / Send Button", null);
		
		runOnNextScreen(this, 
			new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(), 
							getResources().getText(R.string.invite_success_body), Toast.LENGTH_SHORT);

					toast.show();							
				}
			}
		);
		
		goBackToGabList();
	}
	
	@Override
	public void onFailure() {
		closeProgressDialog();
		//ask to retry
		new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
		.setTitle(R.string.invite_failure_dialog_retry_title)
		.setMessage(R.string.invite_failure_dialog_retry_body)
		.setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				inviteFragment.sendInvite();
			}
		})
		.setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				goBackToGabList();
			}	    	    
		})
		.show();
	}
	
	private void closeProgressDialog() {
		if(dialog != null) {
			dialog.dismiss();			
		}

		observer.stopListening();
	}
}
