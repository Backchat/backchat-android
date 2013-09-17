package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;
import com.youtell.backdoor.models.Message;

import android.os.Bundle;
import android.view.View;

/**
 * An activity representing a single Gab detail screen. 
 */
public class GabDetailActivity extends BaseGabDetailActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(gab.isNewAndEmpty())
        	setupActionBar(SHOW_CANCEL_BUTTON);
        else
        	setupActionBar(SHOW_DELETE_BUTTON);
              
        if (savedInstanceState == null) {
        	setupFragment(R.drawable.green_bubble, R.drawable.green_bubble);
        }    
    } 
    
    public void onCancelClick(View v) {
    	goUp();
    }
    
	@Override
	public void beforeMessageSend(Message message) {
		super.beforeMessageSend(message);
		if(gab.isNewAndEmpty()) {
			setupActionBar(SHOW_DELETE_BUTTON);
		}
	}   
	
    @Override
    protected void goUp() {
    	if(gab.isNewAndEmpty())
    		gab.remove();
    	super.goUp();
    }
}
