package com.youtell.backdoor;

import com.youtell.backdoor.dummy.DummyContent;
import com.youtell.backdoor.models.Gab;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * An activity representing a single Gab detail screen. 
 */
public class GabDetailActivity extends FragmentActivity {

	private Gab gab;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gab_detail);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        String gabID = getIntent().getStringExtra(GabDetailFragment.ARG_ITEM_ID);
        
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(GabDetailFragment.ARG_ITEM_ID,
                    gabID);
            GabDetailFragment fragment = new GabDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.gab_detail_container, fragment)
                    .commit();
        }
        
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
                        
		//we have to custom layout the params because the custom view can't affect the parent in XML
        View cView = getLayoutInflater().inflate(R.layout.gab_detail_activity_bar_layout, null);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        actionBar.setCustomView(cView, lp);
        
        this.gab = DummyContent.ITEM_MAP.get(gabID);
        if(gab != null) {
        	setTitle(gab.getTitle());
        	if(!gab.isAnonymous()) {
        		//hide the two buttons
        		View clueButton = cView.findViewById(R.id.gab_clue_button);
        		clueButton.setVisibility(View.GONE);
        		View tagButton = cView.findViewById(R.id.gab_tag_button);
        		tagButton.setVisibility(View.GONE);
        	}
        }        
    }

    public void tagGab(View v) {
    	final EditText tagName = new EditText(this);

    	// Set the default text to a link of the Queen
    	tagName.setText(gab.getRelatedUserName());

    	new AlertDialog.Builder(this)
    	  .setTitle("Tag")
    	  .setMessage("Tag this conversation")
    	  .setView(tagName)
    	  .setPositiveButton("Save", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	      gab.setRelatedUserName(tagName.getText().toString());
    	    }
    	  })
    	  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	    }
    	  })
    	  .show(); 	    
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, GabListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
