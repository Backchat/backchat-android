package com.youtell.backdoor;

import com.youtell.backdoor.dummy.DummyContent.DummyItem;

import android.content.Context;
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
			
	public Tile(Context context, ViewGroup parent) {
		this.context = context;
		this.parentGroup = parent;
	}

	public View getViews()
	{
		 this.views = LayoutInflater.from(context).inflate(R.layout.tile_row_layout, parentGroup, false);
		 this.titleLabel = (TextView) this.views.findViewById(R.id.tile_title);
		 this.subtitleLabel = (TextView) this.views.findViewById(R.id.tile_subtitle);
		 this.icon = (ImageView) this.views.findViewById(R.id.tile_icon);
		 this.attributeIcon = (ImageView) this.views.findViewById(R.id.tile_attribute_icon);
		 this.views.setTag(this);

		 return views;
	}

	public void fillWithGab(DummyItem message) {
		this.titleLabel.setText(message.content);
		this.subtitleLabel.setText("A subtitle");
	}
}
