package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;

import android.os.Bundle;

/**
 * An activity representing a single Gab detail screen. 
 */
public class GabDetailActivity extends BaseGabDetailActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar(SHOW_DELETE_BUTTON);
              
        if (savedInstanceState == null) {
        	setupFragment(R.drawable.green_bubble, R.drawable.green_bubble);
        }    
    } 
}
