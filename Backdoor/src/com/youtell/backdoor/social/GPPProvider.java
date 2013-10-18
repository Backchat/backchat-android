package com.youtell.backdoor.social;

import java.io.IOException;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;
import com.youtell.backdoor.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class GPPProvider extends SocialProvider {
	private final static int REQUEST_CODE_RESOLVE_ERR = 1;
	private final static String TAG = "GPPProvider";

	private PlusClient gppClient;
	private ConnectionResult gppConnectionResult;
	private String token;

	public GPPProvider(Callback c) {
		super(c);
	}

	private void buildGppClient(final Activity activity) {
		gppClient = new PlusClient.Builder(activity.getApplicationContext(), new ConnectionCallbacks() {
			@Override
			public void onConnected(Bundle arg0) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							GPPProvider.this.token = GoogleAuthUtil.getToken(activity, gppClient.getAccountName(), 
									"oauth2: https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/plus.login");
							//connected to GPP!
							Log.e(TAG, token);
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									GPPProvider.this.callback.onAuthenticated(GPPProvider.this);
								}
							});
						} catch (UserRecoverableAuthException e) {
							Log.e(TAG, "recoverexception", e);
							Intent recover = e.getIntent();
							activity.startActivityForResult(recover, REQUEST_CODE_RESOLVE_ERR);
						} catch (IOException e) {
							Log.e(TAG, "ioexception", e);
						} catch (GoogleAuthException e) {
							Log.e(TAG, "authexception", e);
						}			
					}
				});

				t.start();				
			}

			@Override
			public void onDisconnected() {
				// don't care				
			}
			
		}, new OnConnectionFailedListener() {
			@Override
			public void onConnectionFailed(ConnectionResult result) {
				if (result.hasResolution()) {
					try {
						result.startResolutionForResult(activity, REQUEST_CODE_RESOLVE_ERR);
					} catch (SendIntentException e) {
						gppClient.connect();
					}
				}
				
				// Save the result and resolve the connection failure upon a user click.
				gppConnectionResult = result;
				callback.onFailedLogin();				
			}
			
		})
		.setScopes("https://www.googleapis.com/auth/plus.login", "https://www.googleapis.com/auth/userinfo.email") // https://www.googleapis.com/auth/userinfo.profile 
		.build();
		
		gppClient.connect();
	}
	
	@Override
	public void tryCachedLogin(Activity act) {
		buildGppClient(act);
	}

	@Override
	public void logout() {
		if(gppClient != null) {
			gppClient.clearDefaultAccount();
		}
		else {
			Log.e(TAG, "Logout with NULL GPPCLIENT");
		}
		
		disconnect();
	}

	private class GPPShareHelper implements ShareHelper
	{
		private Activity activity;
		
		public GPPShareHelper(Activity act) {	
			activity = act;
		}

		@Override
		public void onCreate(Bundle state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSaveInstanceState(Bundle state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shareApp() {
			PlusShare.Builder builder = new PlusShare.Builder(activity, gppClient);
			Uri uri = Uri.parse(activity.getString(R.string.share_app_link));
			builder.addCallToAction(
					"TRY_IT",
					uri,
					"");

			// Set the content url (for desktop use).
			builder.setContentUrl(uri).
				setType("text/plain").
				setText(activity.getString(R.string.gpp_share_text));

			activity.startActivityForResult(builder.getIntent(), 0);
		}
	}
	
	@Override
	public ShareHelper getShareHelper(Activity activity) {
		return new GPPShareHelper(activity);
	}

	@Override
	public void login(Activity act) {
		buildGppClient(act);
	}

	@Override
	public void onActivityResult(Activity act, int requestCode,
			int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
			if(resultCode == Activity.RESULT_OK) {
				gppConnectionResult = null;
				gppClient.connect();
			}
			else {
				callback.onFailedLogin();
			}
		}		
	}

	@Override
	public void disconnect() {
		if(gppClient != null) {
			gppClient.disconnect();
			gppClient = null;
		}
	}

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public String getProviderName() {
		return GPP_PROVIDER;
	}

}