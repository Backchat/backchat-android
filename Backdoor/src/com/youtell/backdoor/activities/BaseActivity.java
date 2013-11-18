package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;
import com.youtell.backdoor.observers.GCMToastNotificationObserver;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {
	protected GCMToastNotificationObserver toastObserver;
	
	public BaseActivity() {
		super();
		toastObserver = new GCMToastNotificationObserver(this);
	
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	goUp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }	
	
	protected void setupTitleActionBar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);	
	}
	
	protected void goUp() {
        NavUtils.navigateUpFromSameTask(this);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	@Override
	public void onBackPressed() { //make back up for us.
		goUp();
	}
	
	static public void runOnNextScreen(final Activity activity, final Runnable runnable) {
		new Handler().postDelayed(
				new Runnable() {
					@Override
					public void run() {
						activity.runOnUiThread(runnable);
					}
				},
				activity.getResources().getInteger(android.R.integer.config_longAnimTime)
				);		
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
		toastObserver.startListening();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		toastObserver.stopListening();
	}
	
}
