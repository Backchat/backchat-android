package com.youtell.backchat;

public class Settings {
	public String mixpanelToken;
	public String hockeyToken;
	public String apiServerName;
	public String gcmKey;
	public String loginApiServerName;
	
	public boolean hideUserDataExceptions;
	public boolean alwaysShowTour;
	public boolean alwaysWipeDB;
	public boolean slowInternet;	
	
	public Settings(String mixpanel, String hockey, String gcm, String servername, String loginserver, boolean show, boolean wipe, boolean hide, boolean slow) {
		mixpanelToken = mixpanel;
		hockeyToken = hockey;
		apiServerName = servername;
		alwaysShowTour = show;
		alwaysWipeDB = wipe;
		gcmKey = gcm;
		hideUserDataExceptions = hide;
		slowInternet = slow;
		loginApiServerName = loginserver;
	}
	
	public static final Settings debugSettings = 
			new Settings("06e542f53ddbd2430aac9c4664e5903f", "205f05f38db1a0bf5d795fa469d91cbe", "535388359184", 
					"backchat-stage.herokuapp.com", "backchat-login-stage.herokuapp.com", false, true, false, true); 

	public static final Settings productionSettings =
			new Settings("b773675a02695460a27af2b8c2d11d39", "3509cdc42bfd782598bcbbc6907fd661", "334350433648", 
					"api.getbackchat.com", "login.getbackchat.com", false, false, true, false);
	
	public static final long internetDelay = 5000;
	
	public static Settings settings = debugSettings;	
}
