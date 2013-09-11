package com.youtell.backdoor;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * An activity representing a list of Gabs. 
 * 
 * The activity makes heavy use of fragments. The list of items is a
 * {@link GabListFragment} and the item details
 * (if present) is a {@link GabDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link GabListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class GabListActivity extends BaseActivity
        implements GabListFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gab_list);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        View cView = getLayoutInflater().inflate(R.layout.gab_list_activity_bar_layout, null);
        actionBar.setCustomView(cView);
    }
    
    /**
     * Callback method from {@link GabListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
    	// In single-pane mode, simply start the detail activity
    	// for the selected item ID.
    	Intent detailIntent = new Intent(this, GabDetailActivity.class);
    	detailIntent.putExtra(GabDetailFragment.ARG_ITEM_ID, id);
    	startActivity(detailIntent);
    }
}
