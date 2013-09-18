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

    	new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
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
        setupActionBar(SHOW_ANON_BUTTONS | SHOW_DELETE_BUTTON);
                
        if(savedInstanceState == null) {
        	setupFragment(R.drawable.blue_bubble, R.drawable.black_bubble_from, R.color.blue_bubble_text_color, R.color.black_bubble_from_text_color);
        }        
    }
}
