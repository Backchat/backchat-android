package com.youtell.backdoor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.DateUtils;

public class Util {
	public static String humanDateTime(Context context, Date input)
	{
		Date today = new Date();
		long todayTime = today.getTime(); 
		long distance = todayTime - input.getTime();
		if(distance < 24*60*60*1000)
			return DateUtils.formatDateTime(context, input.getTime(), DateUtils.FORMAT_SHOW_TIME);
		if(distance > 24*60*60*1000 && distance <= 7*24*60*60*1000) //excluding leap seconds...
			return DateUtils.formatDateTime(context, input.getTime(), DateUtils.FORMAT_SHOW_WEEKDAY);
		else
			return DateUtils.formatDateTime(context, input.getTime(), DateUtils.FORMAT_SHOW_DATE);
	}
	
	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "???";
		}
	}

	
	public static Date parseJSONDate(String string) throws JSONException {
		//TODO perf
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
		try {
			return formatter.parse(String.format("%s UTC", string));
		} catch (ParseException e) {
			throw new JSONException("bad date");
		}
	}
	

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();

	public static String generatePseudoRandomString(int len) {
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
			sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		return sb.toString();

	}
}
