package com.youtell.backdoor.social;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mixpanel.android.mpmetrics.MixpanelAPI.People;
import com.youtell.backdoor.Application;
import com.youtell.backdoor.api.PostFreeShareClueRequest;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.services.APIService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class SocialProvider {
	public interface Callback {
		public void onAuthenticated(SocialProvider provider);
		public void onFailedLogin();
	}
	
	protected Callback callback;
	
	protected SocialProvider(Callback callback) {
		this.callback = callback;
	}	
	
	abstract public void tryCachedLogin(Activity activity);
	abstract public void login(Activity activity);
	abstract public void logout();
	abstract public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
	abstract public void disconnect();	
	abstract public String getToken();
	abstract public String getProviderName();
	abstract public void getUserInfo(Activity activity);
	
	public interface ShareHelper {
		abstract public void onCreate(Bundle state);
		abstract public void onResume();
		abstract public void onPause();
		abstract public void onDestroy();
		abstract public void onSaveInstanceState(Bundle state);
		abstract public void onActivityResult(int requestCode, int resultCode, Intent data);
		abstract public void shareApp();
	}
	
	public interface ShareCallback {
		abstract public void onSuccess();
		abstract public void onFailure();
	}
	
	abstract public ShareHelper getShareHelper(Activity act);
	
	public static final String FB_PROVIDER = "facebook";
	public static final String GPP_PROVIDER = "gpp";
	
	public static <T extends Activity & Callback> SocialProvider createByProviderName(String providerName, T activity) {
		if(providerName == null)
			return null;
		
		if(providerName.equals(FB_PROVIDER))
			return new FacebookProvider(activity);
		else if(providerName.equals(GPP_PROVIDER))
			return new GPPProvider(activity);
		else
			return null;
	}
	
	private static SocialProvider current;
	
	public static SocialProvider getActiveProvider() {
		return current;
	}
	
	public static void setActiveProvider(SocialProvider newProvider) {
		current = newProvider;
	}
	
	protected static void onSuccessShare(ShareCallback callback) 
	{
		APIService.fire(new PostFreeShareClueRequest()); //TODO make this actually wait?
		callback.onSuccess();
	}
	
	protected int calculateAge(String birthday, String format) {
		if(birthday.length() > 0) {
			Date date;
			try {
				date = new SimpleDateFormat(format).parse(birthday);
				Date now = new Date();
				long diff = now.getTime() - date.getTime();
				int years = (int) ((double)diff / 1000.0 / 60.0 / 60.0 / 24.0 / 365.0);
				return years;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
		
		return 0;
	}
	
	protected void registerMixpanelGCM(People p) {
		p.setPushRegistrationId(User.getCurrentUser().getDeviceID());
	}
}
