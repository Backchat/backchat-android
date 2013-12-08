package com.youtell.backchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.youtell.backchat.social.SocialProvider;
import com.youtell.backchat.R;

public class TourActivity extends BaseActivity implements SocialProvider.ShareCallback {
	private SocialProvider.ShareHelper shareHelper;

	private class SwipeDetector extends SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		private TourActivity tour;
		
		public SwipeDetector(TourActivity tour) {
			this.tour = tour;
		}
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				tour.onNext();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				tour.onPrev();
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	private int page;
	
	private void selectRadio() {
		radios.check(-1);
		switch(page) {
		case 0: radios.check(R.id.tour_page_1_radio);
		break;
		case 1: radios.check(R.id.tour_page_2_radio);
		break;
		case 2: radios.check(R.id.tour_page_3_radio);
		break;
		}
	}
	
	public void onNext() {
		if(page == 2)
			return;
		
		page++;
		selectRadio();
		
		flipper.showNext();
	}
	
	public void onPrev() {
		if(page == 0)
			return;
		
		page--;
		
		selectRadio();
		flipper.showPrevious();
	
	}
	
	private ViewFlipper flipper;
	private RadioGroup radios;
	private GestureDetector swiper;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tour);

		flipper = (ViewFlipper) findViewById(R.id.tour_flipper);
		swiper = new GestureDetector(this, new SwipeDetector(this));
			
		final Button shareButton = (Button) findViewById(R.id.tour_button_share);
		final Button closeButton = (Button) findViewById(R.id.tour_button_cancel);

		final ImageView tour_3_view = (ImageView) findViewById(R.id.tour_3_view);

		radios = (RadioGroup) findViewById(R.id.tour_radio);

		SocialProvider provider = SocialProvider.getActiveProvider();

		shareHelper = provider.getShareHelper(this);

		shareHelper.onCreate(savedInstanceState);

		if(provider.getProviderName().equals(SocialProvider.FB_PROVIDER)) {
			shareButton.setText(R.string.tour_page_3_share_fb);
			tour_3_view.setBackgroundResource(R.drawable.tour_3_facebook);
		}
		else {
			shareButton.setText(R.string.tour_page_3_share_gpp);
			tour_3_view.setBackgroundResource(R.drawable.tour_3_google);
		}

		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareHelper.shareApp();
			}

		});

		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goUp();
			}

		});

		//set default to false
		for(int i = 0; i < radios.getChildCount(); i++){
			((RadioButton)radios.getChildAt(i)).setEnabled(false);
		}

		radios.check(R.id.tour_page_1_radio);

		flipper.setOnTouchListener(new OnTouchListener() {

			   public boolean onTouch(View v, MotionEvent event) {
			    return !swiper.onTouchEvent(event);
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		shareHelper.onResume();
	}

	@Override
	protected void onDestroy() 
	{
		shareHelper.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		shareHelper.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		shareHelper.onSaveInstanceState(state);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		shareHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSuccess() {
		goUp();
	}

	@Override
	public void onFailure() {
		goUp();
	}


}
