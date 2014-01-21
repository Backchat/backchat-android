package com.youtell.backchat.activities;

import com.youtell.backchat.Application;
import com.youtell.backchat.models.Message;
import com.youtell.backchat.R;

import android.os.Bundle;
import android.view.View;

/**
 * An activity representing a single Gab detail screen. 
 */
public class GabDetailActivity extends BaseGabDetailActivity {
	@Override
	public void onChange(String action, int gabID) {
		super.onChange(action, gabID);
		updateActionBar();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        if (savedInstanceState == null) {
        	setupFragment(R.drawable.black_bubble_to, R.drawable.green_bubble, R.drawable.send_black_button_selector, R.color.black_bubble_from_text_color, R.color.green_bubble_text_color);
        }    
        
        updateActionBar();
    } 
    
    private void updateActionBar() {
    	if(gab.isNew()) {
        	setupActionBar(SHOW_CANCEL_BUTTON);

		}
		else {/*TODO if(gab.isRemoteObject()) { */
        	setupActionBar(SHOW_DELETE_BUTTON);		
		}
		/*else {
			setupActionBar(0);
		}*/
    }
    
    public void onCancelClick(View v) {
    	goUp();
    }    
	
    @Override
    protected void goUp() {
    	if(gab.isNew()) {
        	Application.mixpanel.track("Cancelled Thread Compose", null);
    		gab.remove();
    	}
    	super.goUp();
    }
}
