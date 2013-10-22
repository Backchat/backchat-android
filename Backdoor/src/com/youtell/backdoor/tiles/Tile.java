package com.youtell.backdoor.tiles;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.youtell.backdoor.R;
import com.youtell.backdoor.R.id;
import com.youtell.backdoor.R.layout;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class Tile {
	private View views;
	protected Context context;
	private ViewGroup parentGroup;
	
	protected TextView titleLabel;
	protected TextView subtitleLabel;
	protected ImageView icon;
	protected ImageView attributeIcon;
	protected TextView timeLabel;
			
	public Tile(Context context, ViewGroup parent) {
		this.context = context;
		this.parentGroup = parent;
	}

	public Tile(Context context, View existing) 
	{
		this.context = context;
		this.views = existing;
		findViews();
	}
	
	public View getViews()
	{
		 this.views = LayoutInflater.from(context).inflate(R.layout.tile_row_layout, parentGroup, false);
		 findViews();
		 return views;
	}
	
	private void findViews()
	{
		 this.titleLabel = (TextView) this.views.findViewById(R.id.tile_title);
		 this.subtitleLabel = (TextView) this.views.findViewById(R.id.tile_subtitle);
		 this.icon = (ImageView) this.views.findViewById(R.id.tile_icon);
		 this.attributeIcon = (ImageView) this.views.findViewById(R.id.tile_attribute_icon);
		 this.timeLabel = (TextView) this.views.findViewById(R.id.tile_time);		
		 this.views.setTag(this);
	}

	protected void loadAvatar(String uri)
	{
		int rounding = (int)context.getResources().getDimension(R.dimen.tile_avatar_rounding);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)				
				.displayer(new RoundedBitmapDisplayer(rounding))
				.build();
		
		ImageLoader.getInstance().displayImage(uri, this.icon, options);	
	}
	
	protected void setAlpha(double a) {
		float alpha = (float)a;
		this.titleLabel.setAlpha(alpha);
		this.subtitleLabel.setAlpha(alpha);
		this.icon.setAlpha(alpha);
		this.attributeIcon.setAlpha(alpha);
		this.timeLabel.setAlpha(alpha);
	}
	
	public abstract void fill(Object object);
}
