package com.youtell.backdoor.activities;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostLoginRequest;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.APIRequestObserver;
import com.youtell.backdoor.observers.APIRequestObserver.Observer;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.services.APIService;

public class LoginActivity extends BaseActivity implements Observer, UserObserver.Observer,
GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private static final String PREFS_LOGIN = "PREFS_LOGIN";
	private static final String FB_PROVIDER = "facebook";
	private static final String GPP_PROVIDER = "gpp";
	private static final String CACHED_SOCIAL = "CACHED_SOCIAL";
	private APIRequestObserver<PostLoginRequest> observer = new APIRequestObserver<PostLoginRequest>(this, PostLoginRequest.class);
	private Object userObserver;
	private ProgressDialog progressDialog;
	private PlusClient gppClient;
	private ConnectionResult gppConnectionResult;
	private boolean gppLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		toastObserver.disable();

		setContentView(R.layout.activity_login);
		setButtonVisibility(View.GONE);

		observer.startListening();
		userObserver = UserObserver.registerObserver(this);
		Log.e("login", "login");

		//if we got here, that means we don't have a cached BD login.
		//check to see if we have stored in shared prefs the social provider we last used.
		SharedPreferences prefs = getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
		String cachedProvider = prefs.getString(CACHED_SOCIAL, "");
		Log.e("login", cachedProvider);
		
		if(cachedProvider.equals(FB_PROVIDER)) {
			/* try no UI fb cached auth */
			Session fbSession = Session.openActiveSessionFromCache(this);
			if(fbSession != null) {
				/* success! */
				loginUser(fbSession.getAccessToken(), FB_PROVIDER);
			}
			else {
				/* failure; don't try GPP per our prefs, so show our buttons */
				setButtonVisibility(View.VISIBLE);			
			}
		}
		else if(cachedProvider.equals(GPP_PROVIDER)) {
			gppLogin = true;
			connectGPPClient();
		}
		else {
			/* nothing cached */
			setButtonVisibility(View.VISIBLE);	
		}
	}

	private void connectGPPClient()
	{
		gppClient = new PlusClient.Builder(this, this, this).setScopes("https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile")
				.build();
		gppClient.connect();
	}

	private void loginUser(String token, String provider) {
		/* save the provider preference */
		SharedPreferences prefs = getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putString(CACHED_SOCIAL, provider);
		edit.commit();

		/* attempt to login and throw up a progress dialog */
		progressDialog = ProgressDialog.show(this, "Logging in", null, true, false); //TODO stringify
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		lp.copyFrom(progressDialog.getWindow().getAttributes());
		lp.width = 675;
		lp.height = 500;
		progressDialog.getWindow().setAttributes(lp);
		APIService.fire(new PostLoginRequest(token, provider, "backdoor-stage.herokuapp.com"));
	}

	public void setButtonVisibility(int v) {
		findViewById(R.id.login_facebook_button).setVisibility(v);
		findViewById(R.id.login_gpp_button).setVisibility(v);
	}

	public void fbButtonClick(View v)
	{
		/* if we're at a button, means we definitely need UI now: */
		Session.openActiveSession(this, true, new StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {	
				if(state.isOpened()) {
					/* the user didn't cancel, etc. */
					loginUser(session.getAccessToken(), FB_PROVIDER);
				}

			}			
		});		
	}

	public void gppButtonClick(View v)
	{
		gppLogin = true;
		connectGPPClient();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(Session.getActiveSession() != null)
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
			gppClient.connect();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UserObserver.unregisterObserver(userObserver);
		observer.stopListening();
		if(gppClient != null)
			gppClient.disconnect();
	}

	@Override
	public void onSuccess() {
		//use user swap instead
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

	}

	@Override
	public void onUserChanged() {
	}

	@Override
	public void onUserSwapped(User old, User newUser) {
		Log.e("login", String.format("%s %s", old != null ? "yes":"no", newUser != null? "yes":"noe"));
		if(newUser != null) {
			//yes!
			if(progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

			Intent intent = null;	
			intent = new Intent(this, GabListActivity.class);
			startActivity(intent);
			finish();

			runOnNextScreen(
					new Runnable() {
						@Override
						public void run() {
							Toast toast = Toast.makeText(getApplicationContext(), 
									getResources().getText(R.string.login_success), Toast.LENGTH_SHORT); //TODO add name

							toast.show();							
						}
					}
					);
		}

		else {
			//we are logging outut, yo! note that this is called before we try to log in in onCreate
			SharedPreferences prefs = getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
			String provider = prefs.getString(CACHED_SOCIAL, "");

			if(provider == FB_PROVIDER) {
				if(Session.getActiveSession() != null) {
					Session.getActiveSession().closeAndClearTokenInformation();
				}
			}
			else if(provider == GPP_PROVIDER) {
				gppLogin = false;
				connectGPPClient(); /* we are logged in so silent auth will succeed */
			}

			//remove the shared prefs
			Editor edit = prefs.edit();
			edit.putString(CACHED_SOCIAL, "");
			edit.commit();
		}
	}

	//TODO
	static int REQUEST_CODE_RESOLVE_ERR = 1;

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		//if (mConnectionProgressDialog.isShowing()) {
		// The user clicked the sign-in button already. Start to resolve
		// connection errors. Wait until onConnected() to dismiss the
		// connection dialog.
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				gppClient.connect();
			}
		}
		// Save the result and resolve the connection failure upon a user click.
		gppConnectionResult = result;		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if(gppLogin) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// TODO Auto-generated method stub
						final String token = GoogleAuthUtil.getToken(LoginActivity.this, gppClient.getAccountName(), 
								"oauth2: https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile");
						//connected to GPP!
						Log.e("GPP", token);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								loginUser(token, GPP_PROVIDER);							
							}

						});

					} catch (UserRecoverableAuthException e) {
						// TODO Auto-generated catch block
						Log.e("GPP", "exception", e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e("GPP", "exception", e);
					} catch (GoogleAuthException e) {
						// TODO Auto-generated catch block
						Log.e("GPP", "exception", e);
					}			
				}
			});
			t.start();
		}
		else {
			gppClient.clearDefaultAccount();
			gppClient.disconnect();
		}
	}

	@Override
	public void onDisconnected() {
		// don't care
	}
}
