package com.youtell.backdoor.social;

import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Name;
import com.google.android.gms.plus.model.people.Person.PlacesLived;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mixpanel.android.mpmetrics.MixpanelAPI.People;
import com.youtell.backdoor.Application;
import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostUserDataRequest;
import com.youtell.backdoor.services.APIService;

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
									Application.mixpanel.track("Signed In With Google+", null);
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
		private ShareCallback callback;

		public GPPShareHelper(Activity act) {	
			activity = act;
			callback = (ShareCallback)act;
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
			if(resultCode == Activity.RESULT_OK) {
				Application.mixpanel.track("Shared On Google+", null);
				onSuccessShare(callback);
			}
			else {
				Application.mixpanel.track("Cancelled Google+ Share", null);
				callback.onFailure();
			}				
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

	@Override
	public void getUserInfo(Activity activity) {
		final String email = gppClient.getAccountName();
		final Person person = gppClient.getCurrentPerson();
		if(person == null)
			return;

		JSONObject result = new JSONObject();
		try {
			result.put("email", email);

			if(person.hasGender())
				result.put("gender", person.getGender() == Person.Gender.MALE ? "male" : "female");

			if(person.hasPlacesLived()) {
				JSONArray placesJSON = new JSONArray();
				for(PlacesLived aPlace : person.getPlacesLived()) {
					JSONObject place = new JSONObject();
					place.put("primary", aPlace.isPrimary() ? "1" : "0");
					place.put("value", aPlace.getValue());
					placesJSON.put(place);
				}
				result.put("placesLived", placesJSON);
			}

			if(person.hasOrganizations()) {
				JSONArray organizationsJSON = new JSONArray();
				for(Person.Organizations aOrg : person.getOrganizations()) {
					JSONObject org = new JSONObject();
					org.put("type", aOrg.getType() == Person.Organizations.Type.WORK ? "work" : "school");
					org.put("primary", aOrg.isPrimary());
					org.put("name", aOrg.getName());
					organizationsJSON.put(org);
				}
				result.put("organizations", organizationsJSON);
			}

			result.put("displayName", person.getDisplayName());

			APIService.fire(new PostUserDataRequest(PostUserDataRequest.GPPData, result));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				People p = Application.mixpanel.getPeople();

				p.append("$created", new Date());

				if(person.hasGender())
					p.set("Gender", person.getGender() == Person.Gender.MALE ? "Male" : "Female");

				if(person.hasBirthday()) 
					p.set("age", calculateAge(person.getBirthday(), "yyyy-MM-dd"));

				JSONObject obj = new JSONObject();
				try {
					Name name = person.getName();
					if(name != null) {
						obj.put("$first_name", name.getGivenName());

						obj.put("$last_name", name.getFamilyName());
					}

					obj.put("$email", email);
					obj.put("Google+ Id", person.getId());
					p.set(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}	
}
