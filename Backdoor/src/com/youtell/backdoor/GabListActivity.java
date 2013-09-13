package com.youtell.backdoor;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

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
public class GabListActivity extends SlidingActivity
        implements GabListFragment.Callbacks {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gab_list);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        View cView = getLayoutInflater().inflate(R.layout.gab_list_activity_bar_layout, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT);
        actionBar.setCustomView(cView, lp);      
        
        int ID_MENUFRAME = 1001110101;
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(ID_MENUFRAME);
        setBehindContentView(frameLayout);
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        SettingsMenuFragment menuFragment = new SettingsMenuFragment();
        ft.replace(ID_MENUFRAME, menuFragment);
        ft.commit();
        
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindOffsetRes(R.dimen.sliding_menu_offset);
        sm.setShadowWidthRes(R.dimen.sliding_menu_shadow_width);
        sm.setShadowDrawable(R.drawable.sliding_menu_shadow);
    }
    
    public void settingsClick(View v) {
    	getSlidingMenu().toggle();
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
