package com.youtell.backdoor.activities;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.youtell.backdoor.R;
import com.youtell.backdoor.fragments.GabListFragment;
import com.youtell.backdoor.fragments.SettingsMenuFragment;
import com.youtell.backdoor.models.Gab;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

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
    
    public void newGabClick(View v) {
    	Intent intent = new Intent(this, NewGabActivity.class);
    	startActivity(intent);
    }
    
    @Override
    public void onItemSelected(Gab gab) {
    	gab.startDetailIntent(this);
    }
}
