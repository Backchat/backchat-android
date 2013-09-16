package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;

import android.os.Bundle;
import android.view.View;

/**
 * An activity representing a single Gab detail screen. 
 */
public class GabDetailActivity extends BaseGabDetailActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(gab.isNew())
        	setupActionBar(SHOW_CANCEL_BUTTON);
        else
        	setupActionBar(SHOW_DELETE_BUTTON);
              
        if (savedInstanceState == null) {
        	setupFragment(R.drawable.green_bubble, R.drawable.green_bubble);
        }    
    } 
    
    public void onCancelClick(View v) {
    	gab.delete();
    	goUp();
    }
}
