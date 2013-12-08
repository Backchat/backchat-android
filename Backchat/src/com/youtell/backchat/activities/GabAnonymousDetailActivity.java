package com.youtell.backchat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.youtell.backchat.Application;
import com.youtell.backchat.fragments.GabCluesFragment;
import com.youtell.backchat.iap.BuyClueIAP;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.UserObserver;
import com.youtell.backchat.R;

public class GabAnonymousDetailActivity extends BaseGabDetailActivity implements BuyClueIAP.Observer, GabCluesFragment.Callbacks {
	private GabCluesFragment cluesFragment;
	private BuyClueIAP buyClue = new BuyClueIAP(this);

	public void tagGab(View v) {
    	final EditText tagName = new EditText(this);

    	tagName.setText(gab.getRelatedUserName());

    	new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
    	  .setTitle(R.string.tag_dialog_title)
    	  .setMessage(R.string.tag_dialog_body)
    	  .setView(tagName)
    	  .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	      gab.setRelatedUserName(tagName.getText().toString());
    	      gab.save();
    	      gab.updateTag(); //TODO?
    	    }
    	  })
    	  .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	    }
    	  })
    	  .show(); 	    
    }    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(SHOW_ANON_BUTTONS | SHOW_DELETE_BUTTON);
                
        if(savedInstanceState == null) {
        	setupFragment(R.drawable.blue_bubble, R.drawable.black_bubble_from, R.drawable.send_blue_button_selector, R.color.blue_bubble_text_color, R.color.black_bubble_from_text_color);
        }        
              
		Bundle arguments = new Bundle();
		arguments.putInt(GabCluesFragment.ARG_GAB_ID, gabID);
		cluesFragment = new GabCluesFragment();
		cluesFragment.setArguments(arguments);
		
    }
    
    public void onCluesClick(View v) {
    	//hide the keyboard first..
	    InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		
        cluesFragment.show(getFragmentManager(), "CLUES");
    }
       
    @Override
    public void onResume()
    {
    	super.onResume();
    	buyClue.connect();
    }

	@Override
	public void onCancel() {
		cluesFragment.dismiss();
	}	

	@Override
	public void onDestroy() {
		super.onDestroy();
		buyClue.disconnect();
	}
	
	@Override
	public void onBuy() {
		buyClue.present(User.getCurrentUser().clone());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)    
	{
		if(buyClue == null || !buyClue.onActivityResult(requestCode, resultCode, data))			
			super.onActivityResult(requestCode, resultCode, data);
	}	
    
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onReadyIAP() {
		cluesFragment.enableBuyButton();
	}
}
