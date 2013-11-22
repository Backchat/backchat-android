package com.youtell.backdoor.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostLoginRequest;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.APIRequestObserver;
import com.youtell.backdoor.services.APIService;
import com.youtell.backdoor.social.FacebookProvider;
import com.youtell.backdoor.social.GPPProvider;
import com.youtell.backdoor.social.SocialProvider;

public class LoginActivity extends BaseActivity implements APIRequestObserver.Observer<PostLoginRequest>,
SocialProvider.Callback {
	private static final String TAG = "LoginActivity";
	
	private APIRequestObserver<PostLoginRequest> observer = new APIRequestObserver<PostLoginRequest>(this, PostLoginRequest.class);
	
	public static final String FIRST_START_ARG = "FIRST_START_ARG";
	
	private ProgressDialog progressDialog;
	
	private SocialProvider socialProvider;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		toastObserver.disable();

		setContentView(R.layout.activity_login);
		setButtonVisibility(View.GONE);

		observer.startListening();

		Intent args = getIntent();
		boolean firstStart = args.getBooleanExtra(FIRST_START_ARG, false);
		String cachedProvider = null;

		if(!firstStart) {
			//we are logging outut, yo! note that this is called before we try to log in in onCreate
			//if old is NULL AND new is NULL, we haven't checked anything yet.
			SocialProvider.getActiveProvider().logout();
			SocialProvider.setActiveProvider(null);
			User.clearCachedCredentials(getApplicationContext());			
		}
		else {
			//if we got here, that means we don't have a cached BD login.
			//check to see if we have stored in shared prefs the social provider we last used.
			cachedProvider = User.getCachedSocialProvider(getApplicationContext());
			socialProvider = SocialProvider.createByProviderName(cachedProvider, this);
			Log.e(TAG, cachedProvider);
		}
		
		
		if(socialProvider != null) {
			socialProvider.tryCachedLogin(this);
		}
		else {
			/* nothing cached */
			setButtonVisibility(View.VISIBLE);	
		}
	}
	
	@Override
	public void onAuthenticated(SocialProvider provider) {
		/* save the provider preference */
		Log.e(TAG, String.format("caching login %s", provider.getProviderName()));
		User.setCachedSocialProvider(getApplicationContext(), provider);
		/* attempt to login and throw up a progress dialog */
		progressDialog = ProgressDialog.show(this, getResources().getString(R.string.login_dialog_title), null, true, false);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		lp.copyFrom(progressDialog.getWindow().getAttributes());
		lp.width = 675;
		lp.height = 500;
		progressDialog.getWindow().setAttributes(lp);
		APIService.fire(new PostLoginRequest(provider, "backdoor-stage.herokuapp.com"));
	}

	public void setButtonVisibility(int v) {
		findViewById(R.id.login_facebook_button).setVisibility(v);
		findViewById(R.id.login_gpp_button).setVisibility(v);
	}

	public void setButtonState(boolean enabled) {
		findViewById(R.id.login_facebook_button).setEnabled(enabled);
		findViewById(R.id.login_gpp_button).setEnabled(enabled);
	}
	
	public void fbButtonClick(View v)
	{
		setButtonState(false);
		
		socialProvider = new FacebookProvider(this);
		socialProvider.login(this);
	}

	public void gppButtonClick(View v)
	{
		setButtonState(false);

		socialProvider = new GPPProvider(this);
		socialProvider.login(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(socialProvider != null)
			socialProvider.onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public void onSuccess() {
		User.setCachedCredentials(this);
		SocialProvider.setActiveProvider(socialProvider);
		//yes!
		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}


		Intent intent = null;	
		intent = new Intent(this, GabListActivity.class);
		Bundle args = new Bundle();
		args.putInt(GabListActivity.START_ARG, GabListActivity.LOGIN_START);
		intent.putExtras(args);
		startActivity(intent);
		
		finish();	
	}

	@Override
	public void onFailure() {
		//TODO a nice dialog?
		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
		.setTitle(R.string.login_failure_dialog_title)
		.setMessage(R.string.login_failure_dialog_text)
		.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		})
		.show();

		setButtonVisibility(View.VISIBLE);
		setButtonState(true);
	}

	@Override
	public void onFailedLogin()
	{
		setButtonVisibility(View.VISIBLE);
		setButtonState(true);
	}
	
	@Override
	public void goUp() {		
	}
	
}
