package com.youtell.backdoor.activities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.youtell.backdoor.R;
import com.youtell.backdoor.gcm.GCM;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.social.SocialProvider;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class StartupActivity extends android.app.Activity implements SocialProvider.Callback {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = null;
		User u = User.getCachedUser(this);
		if(u != null) {
			//TODO?
			//user.setTotalClueCount(userData.getInt("available_clues"));
			u.setGCMKey(GCM.GCM_KEY); //TODO dynamic merge with postloginrequest
			User.setCurrentUser(u);
			String providerName = User.getCachedSocialProvider(this);
			SocialProvider provider = SocialProvider.createByProviderName(providerName, this);
			provider.tryCachedLogin(this);
			SocialProvider.setActiveProvider(provider);
			
			intent = new Intent(this, GabListActivity.class);


			Toast toast = Toast.makeText(getApplicationContext(), 
					getResources().getText(R.string.login_success), Toast.LENGTH_SHORT); //TODO add name

			toast.show();			
		}
		else {
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(LoginActivity.FIRST_START_ARG, true);
		}
		
		startActivity(intent);
		overridePendingTransition(0,0);	
		finish();
	}

	@Override
	public void onAuthenticated(SocialProvider provider) {		
	}

	@Override
	public void onFailedLogin() {
		// TODO Auto-generated method stub		
	}
}
