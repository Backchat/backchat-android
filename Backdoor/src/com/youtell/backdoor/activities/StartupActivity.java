package com.youtell.backdoor.activities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

public class StartupActivity extends android.app.Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = null;
		if(false) {
			intent = new Intent(this, GabListActivity.class);
		}
		else {
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(LoginActivity.FIRST_START_ARG, true);
		}
		
		startActivity(intent);
		overridePendingTransition(0,0);	
		finish();
	}
}
