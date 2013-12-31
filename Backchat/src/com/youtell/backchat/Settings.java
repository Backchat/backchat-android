package com.youtell.backchat;

public class Settings {
	public String mixpanelToken;
	public String hockeyToken;
	public String apiServerName;
	public String gcmKey;
	
	public boolean hideUserDataExceptions;
	public boolean alwaysShowTour;
	public boolean alwaysWipeDB;
	
	
	public Settings(String mixpanel, String hockey, String gcm, String servername, boolean show, boolean wipe, boolean hide) {
		mixpanelToken = mixpanel;
		hockeyToken = hockey;
		apiServerName = servername;
		alwaysShowTour = show;
		alwaysWipeDB = wipe;
		gcmKey = gcm;
		hideUserDataExceptions = hide;
	}
	
	public static final Settings debugSettings = 
			new Settings("06e542f53ddbd2430aac9c4664e5903f", "205f05f38db1a0bf5d795fa469d91cbe", "535388359184", 
					"backchat-stage.herokuapp.com", false, false, false); 
			//TODO change when we get real settings for production
	public static final Settings productionSettings =
			new Settings("b773675a02695460a27af2b8c2d11d39", "3509cdc42bfd782598bcbbc6907fd661", "334350433648", 
					"api.backchat.com", false, false, true);
	
	public static Settings settings = debugSettings;	
}
