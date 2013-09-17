package com.youtell.backdoor.activities;

import com.youtell.backdoor.R;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	goUp();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
}
