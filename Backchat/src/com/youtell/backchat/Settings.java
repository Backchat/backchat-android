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
			new Settings("b773675a02695460a27af2b8c2d11d39", "205f05f38db1a0bf5d795fa469d91cbe", "412155847073", "backchat-stage.herokuapp.com", false, false, false); 
			//TODO change when we get real settings for production
	public static final Settings productionSettings =
			new Settings("", "", "", "api.backchat.com", false, false, true);
	
	public static Settings settings = debugSettings;	
}
