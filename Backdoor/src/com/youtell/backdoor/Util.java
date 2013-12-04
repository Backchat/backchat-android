package com.youtell.backdoor;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
	
	public static Bitmap openBitmap(String absolutePath, boolean scaled) {
		BitmapFactory.Options o = new BitmapFactory.Options();
        o.inPurgeable = true;
        o.inInputShareable = true;        

        Bitmap myBitmap = BitmapFactory.decodeFile(absolutePath, o);
        ExifInterface exif;
        
		try {
			exif = new ExifInterface(absolutePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		if(scaled) {
            //resize for the thumbnail (220x220 c&p from server)
			double scaleFactor = 220.0 / Math.max(o.outWidth, o.outHeight); 
			int newWidth = (int) (o.outWidth * scaleFactor);
			int newHeight = (int) (o.outHeight * scaleFactor);
			Bitmap oldBitmap = myBitmap;
            Bitmap bmpScaled = Bitmap.createScaledBitmap(myBitmap, newWidth, newHeight, false);
            myBitmap = bmpScaled;
            oldBitmap.recycle();
            oldBitmap = null;
		}
		
        Matrix matrix = new Matrix();
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        if (orientation == ExifInterface.ORIENTATION_NORMAL) {
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
        	matrix.postRotate(90);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
        	matrix.postRotate(180);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
        	matrix.postRotate(270);
        }
        
        Bitmap bmpRotated = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, false);
        
        return bmpRotated;
	}
}
