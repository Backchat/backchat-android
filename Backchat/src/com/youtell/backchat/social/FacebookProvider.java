package com.youtell.backchat.social;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookException;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.StatusCallback;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mixpanel.android.mpmetrics.MixpanelAPI.People;
import com.youtell.backchat.Application;
import com.youtell.backchat.api.PostUserDataRequest;
import com.youtell.backchat.services.APIService;
import com.youtell.backchat.R;

public class FacebookProvider extends SocialProvider implements StatusCallback {
	public FacebookProvider(Callback c) {
		super(c);	
	}

	private Session session;

	@Override
	public void login(Activity activity) {
		Session.openActiveSession(activity, true, this);
	}		

	@Override
	public void logout() {
		if(session != null) {
			session.closeAndClearTokenInformation();
		}		
	}

	private class FBShareHelper implements ShareHelper, StatusCallback, com.facebook.widget.FacebookDialog.Callback {
		private UiLifecycleHelper uiHelper;
		private Activity activity;
		private ShareCallback callback;

		public FBShareHelper(Activity act) {
			this.activity = act;
			this.callback = (ShareCallback)act;
			uiHelper = new UiLifecycleHelper(act, this);
		}

		@Override
		public void onCreate(Bundle state) {
			uiHelper.onCreate(state);
		}

		@Override
		public void onResume() {
			uiHelper.onResume();
		}

		@Override
		public void onPause() {
			uiHelper.onPause();		
		}

		@Override
		public void onDestroy() {
			uiHelper.onDestroy();
		}

		@Override
		public void onSaveInstanceState(Bundle state) {
			uiHelper.onSaveInstanceState(state);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			uiHelper.onActivityResult(requestCode, resultCode, data, this);
		}

		@Override
		public void shareApp() {
			if (FacebookDialog.canPresentShareDialog(activity.getApplicationContext(), 
					FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
				FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(activity)
				.setName(activity.getString(R.string.fb_share_app_name))
				.setCaption(activity.getString(R.string.fb_share_app_caption))
				.setDescription(activity.getString(R.string.fb_share_app_description))
				.setLink(activity.getString(R.string.share_app_link))
				.setPicture(activity.getString(R.string.fb_share_app_picture))
				.build();	

				uiHelper.trackPendingDialogCall(shareDialog.present());
			}
			else {
				Bundle params = new Bundle();
				params.putString("name", activity.getString(R.string.fb_share_app_name));
				params.putString("caption", activity.getString(R.string.fb_share_app_caption));
				params.putString("description", activity.getString(R.string.fb_share_app_description));
				params.putString("link", activity.getString(R.string.share_app_link));
				params.putString("picture", activity.getString(R.string.fb_share_app_picture));

				WebDialog feedDialog = (
						new WebDialog.FeedDialogBuilder(activity,
								Session.getActiveSession(),
								params))
								.setOnCompleteListener(new OnCompleteListener(){
									@Override
									public void onComplete(Bundle values,
											FacebookException error) {
										// TODO Auto-generated method stub

									}})
									.build();
				feedDialog.show();		
			}	
		}

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(PendingCall pendingCall, Bundle data) {
			String gesture = FacebookDialog.getNativeDialogCompletionGesture(data);
			if (gesture != null) {
				if ("post".equals(gesture)) {
					Application.mixpanel.track("Shared On Facebook", null);
					onSuccessShare(callback);
					return;
				}
			}

			Application.mixpanel.track("Cancelled Facebook Share", null);
			callback.onFailure();
		}

		@Override
		public void onError(PendingCall pendingCall, Exception error,
				Bundle data) {
			Application.mixpanel.track("Cancelled Facebook Share", null);
			callback.onFailure();
		}
	}

	@Override
	public ShareHelper getShareHelper(Activity act) {
		return new FBShareHelper(act);
	}

	@Override
	public void tryCachedLogin(Activity activity) {
		/* try no UI fb cached auth */
		this.session = Session.openActiveSessionFromCache(activity);
		if(this.session != null) {
			callback.onAuthenticated(this);
		}
		else
			callback.onFailedLogin();
	}

	@Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		Session.getActiveSession().onActivityResult(activity, requestCode, resultCode, data);
	}

	@Override
	public void disconnect() {
		this.session.closeAndClearTokenInformation();
	}

	@Override
	public String getToken() {
		return session.getAccessToken();
	}

	@Override
	public String getProviderName() {
		return FB_PROVIDER;
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if(state.isOpened()) {
			FacebookProvider.this.session = session;
			/* the user didn't cancel, etc. */
			Application.mixpanel.track("Signed In With Facebook", null);
			callback.onAuthenticated(FacebookProvider.this);
		}
		else if(state.isClosed())
			callback.onFailedLogin();		
	}

	@Override
	public void getUserInfo(Activity activity) {
		String[] names = {null, "family", "interests", "likes"};

		List<Response> responses = com.facebook.Request.executeBatchAndWait(com.facebook.Request.newMeRequest(session, null),
				com.facebook.Request.newGraphPathRequest(session, "me/family", null),
				com.facebook.Request.newGraphPathRequest(session, "me/interests", null),
				com.facebook.Request.newGraphPathRequest(session, "me/likes", null));
		
		try {
		
			JSONObject result = null;

			for(int i=0;i<names.length;i++) {
				Response s = responses.get(i);
				if(s == null || s.getError() != null)
					return;
				
				JSONObject rObj = s.getGraphObject().getInnerJSONObject();
				if(names[i] == null) {
					result = rObj;
				}
				else {
					result.put(names[i], rObj.getJSONArray("data"));

				}			
			}
			
				
			APIService.fire(new PostUserDataRequest(PostUserDataRequest.FacebookData, result));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(String.format("bad exception with JSON %s", e.toString()));
		}

		final GraphUser u = responses.get(0).getGraphObjectAs(GraphUser.class);

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				People p = Application.mixpanel.getPeople();

				if(p == null) {
					Log.e("MIXPANEL", "no person");
					return;
				}
				
				Log.e("MIXPANEL", String.format("set to %s", Application.mixpanel.getDistinctId()));
				p.append("$created", new Date());

				Object gender = u.getProperty("gender");
				if(gender != null) {
					String genderString = gender.toString();
					if(genderString.compareTo("male") == 0)
						p.set("Gender", "Male");
					else
						p.set("Gender", "Female");
				}

				String birthday = u.getBirthday();
				p.set("age", calculateAge(birthday, "MM/dd/yyyy"));

				String firstName = u.getFirstName();
				String lastName = u.getLastName();
				JSONObject obj = new JSONObject();

				final String uid = u.getId();
				Object emailObj = u.getProperty("email");
				String email = "";
				if(emailObj != null) {
					email = emailObj.toString();
				}
				else {
					email = String.format("%s@facebook.com", uid);
				}

				try {
					obj.put("$first_name", firstName);
					obj.put("$last_name", lastName);
					obj.put("Facebook Id", uid);
					obj.put("$email", email);
					p.set(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("MIXPANEL", "exception", e);
				}
				
				registerMixpanelGCM(p);
				
				Application.mixpanel.flush();
			}

		});
	}
}
