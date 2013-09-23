package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;
import com.youtell.backdoor.fragments.GabCluesFragment;
import com.youtell.backdoor.fragments.GabDetailFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class GabAnonymousDetailActivity extends BaseGabDetailActivity implements GabCluesFragment.Callbacks {
	private GabCluesFragment cluesFragment;
	private View cluesView;
	
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
        	setupFragment(R.drawable.blue_bubble, R.drawable.black_bubble_from, R.color.blue_bubble_text_color, R.color.black_bubble_from_text_color);
        }        
        
		Bundle arguments = new Bundle();
		arguments.putInt(GabCluesFragment.ARG_GAB_ID, gabID);
		cluesFragment = new GabCluesFragment();
		cluesFragment.setArguments(arguments);
		getFragmentManager().beginTransaction()
		.add(R.id.gab_clues_container, cluesFragment)
		.commit();
		
		cluesView = findViewById(R.id.gab_clues_container);
    }
    
    public void onCluesClick(View v) {
    	//hide the keyboard first..
	    InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		
    	cluesView.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	cluesView.setVisibility(View.GONE);
    }

	@Override
	public void onCancel() {
		cluesView.setVisibility(View.GONE);
	}
    
}
