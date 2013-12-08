package com.youtell.backchat;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;

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

	public static Bitmap openBitmapFromUri(ContentResolver resolver, String uriString, boolean scaled) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inPurgeable = true;
		o.inInputShareable = true;        

		Uri uri = Uri.parse(uriString);
		BufferedInputStream buffered;
		InputStream stream;
		try {
			stream = resolver.openInputStream(uri);
			buffered = new BufferedInputStream(resolver.openInputStream(uri));
		} catch (FileNotFoundException e) {
			return null;
		}        

		Bitmap myBitmap = BitmapFactory.decodeStream(stream, null, o);

		int orientation = 0;//metadata.get.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(buffered, false);
			ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
			orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);

		} catch (ImageProcessingException e) {
		} catch (IOException e) {
		} catch (MetadataException e) {
		}
		
		buffered = null;
		stream = null;

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
