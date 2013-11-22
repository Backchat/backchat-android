package com.youtell.backdoor.social;

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
import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostUserDataRequest;
import com.youtell.backdoor.services.APIService;

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
			onSuccessShare(callback);
		}

		@Override
		public void onError(PendingCall pendingCall, Exception error,
				Bundle data) {
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
			callback.onAuthenticated(FacebookProvider.this);
		}
		else if(state.isClosed())
			callback.onFailedLogin();		
	}

	@Override
	public void getUserInfo() {
		String[] names = {null, "family", "interests", "likes"};
		try {

			List<Response> responses = com.facebook.Request.executeBatchAndWait(com.facebook.Request.newMeRequest(session, null),
					com.facebook.Request.newGraphPathRequest(session, "me/family", null),
					com.facebook.Request.newGraphPathRequest(session, "me/interests", null),
					com.facebook.Request.newGraphPathRequest(session, "me/likes", null));

			JSONObject result = null;

			for(int i=0;i<names.length;i++) {
				JSONObject rObj = responses.get(i).getGraphObject().getInnerJSONObject();
				if(names[i] == null) {
					result = rObj;
				}
				else {
					result.put(names[i], rObj);
				}			
			}
			APIService.fire(new PostUserDataRequest(PostUserDataRequest.FacebookData, result));
		}
		
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
