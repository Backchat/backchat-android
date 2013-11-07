package com.youtell.backdoor.activities;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.ActionBar;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.youtell.backdoor.R;
import com.youtell.backdoor.models.User;

public class ViewImageActivity extends BaseActivity {
	public static final String IMAGE_URL = "IMAGE_URL";

	private ImageView image;
	private PhotoViewAttacher mAttacher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_image);

		image = (ImageView) findViewById(R.id.view_image_image_view);
		mAttacher = new PhotoViewAttacher(this.image);

		DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new BitmapDisplayer() {
			@Override
			public Bitmap display(Bitmap arg0, ImageView arg1, LoadedFrom arg2) {
		        arg1.setImageBitmap(arg0);
				mAttacher.update();
				return arg0;
			}
				
		})
		.build();

		String uri = String.format("http://%s%s", User.getCurrentUser().getApiServerHostName(), getIntent().getStringExtra(IMAGE_URL));

		ImageLoader.getInstance().displayImage(uri, this.image, options);	
        mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				goUp();
			}});
        
        mAttacher.setScaleType(ScaleType.FIT_CENTER);
        mAttacher.setMaximumScale(4.0f);
	}

	@Override
	public void goUp() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();        
		mAttacher.cleanup();
	}

	
}
