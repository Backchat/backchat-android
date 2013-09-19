package com.youtell.backdoor;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Tile {
	private View views;
	private Context context;
	private ViewGroup parentGroup;
	
	private TextView titleLabel;
	private TextView subtitleLabel;
	private ImageView icon;
	private ImageView attributeIcon;
	private TextView timeLabel;
			
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

	public void fillWithGab(Gab gab) {
		this.titleLabel.setText(gab.getTitle());
		this.subtitleLabel.setText(gab.getContentSummary());
		this.timeLabel.setText(Util.humanDateTime(context, gab.getUpdatedAt()));
		if(gab.isUnread()) {
			this.attributeIcon.setVisibility(View.VISIBLE);
		}
		else {
			this.attributeIcon.setVisibility(View.INVISIBLE);
		}
		
		loadAvatar(gab.getRelatedAvatar());
	}
	
	private void loadAvatar(String uri)
	{
		ImageLoader.getInstance().displayImage(uri, this.icon);	
	}

	public void fillWithFriend(Friend friend) {
		this.titleLabel.setText(friend.getFullName());
		this.subtitleLabel.setVisibility(View.GONE);
		loadAvatar(friend.getAvatar());
		if(friend.isFeatured()) {
		}
		else {
			this.attributeIcon.setVisibility(View.INVISIBLE);
		}
	}

	public void fillWithContact(String name, String number, String photoURI, boolean selected) {
		this.titleLabel.setText(name);		
		this.subtitleLabel.setText(number);
		this.attributeIcon.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
		loadAvatar(photoURI);
	}
}
