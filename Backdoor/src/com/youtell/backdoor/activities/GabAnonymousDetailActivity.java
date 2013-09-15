package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class GabAnonymousDetailActivity extends BaseGabDetailActivity {


    public void tagGab(View v) {
    	final EditText tagName = new EditText(this);

    	tagName.setText(gab.getRelatedUserName());

    	new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
    	  .setTitle(R.string.tag_dialog_title)
    	  .setMessage(R.string.tag_dialog_body)
    	  .setView(tagName)
    	  .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	      gab.setRelatedUserName(tagName.getText().toString());
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
        setupActionBar(true);
                
        if(savedInstanceState == null) {
        	setupFragment(R.drawable.green_bubble, R.drawable.green_bubble);
        }        
    }
}
