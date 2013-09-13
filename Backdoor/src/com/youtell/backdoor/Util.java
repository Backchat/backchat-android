package com.youtell.backdoor;

import java.util.Date;

import android.content.Context;
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
}
