package com.youtell.backdoor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.youtell.backdoor.R;
import com.youtell.backdoor.models.Message;

public class NewGabDetailActivity extends BaseGabDetailActivity {

    public static final String ARG_FRIEND_ID = "ARG_FRIEND_ID";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar(SHOW_CANCEL_BUTTON);
              
        if (savedInstanceState == null) {
        	setupFragment(R.drawable.green_bubble, R.drawable.green_bubble, true);
        }    
    } 
    
	public void onCancelClick(View v) {
		goUp();
	}
	
	@Override
	protected void goUp() {
		gab.delete();
		super.goUp();
	}
	
	@Override
	public void onMessageSend(Message message) {
		//switch to gab detail view.
    	Intent intent = BaseGabDetailActivity.getDetailIntent(this, gab);
    	intent.putExtra(BaseGabDetailActivity.ARG_KEYBOARD_OPEN, true);
    	startActivity(intent);
    	overridePendingTransition(0,0);
    	finish(); //get rid of ourselves.
	} 
}
