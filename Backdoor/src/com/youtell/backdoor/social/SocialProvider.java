package com.youtell.backdoor.social;

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
	
	public interface ShareHelper {
		abstract public void onCreate(Bundle state);
		abstract public void onResume();
		abstract public void onPause();
		abstract public void onDestroy();
		abstract public void onSaveInstanceState(Bundle state);
		abstract public void onActivityResult(int requestCode, int resultCode, Intent data);
		abstract public void shareApp();
	}
	
	abstract public ShareHelper getShareHelper(Activity act);
	
	protected static final String FB_PROVIDER = "facebook";
	protected static final String GPP_PROVIDER = "gpp";
	
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
}
