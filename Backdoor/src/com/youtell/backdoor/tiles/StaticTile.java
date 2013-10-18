package com.youtell.backdoor.tiles;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class StaticTile extends Tile {
	private int titleR;
	private int subtitleR;
	private int iconR;
	public static final int hideResource = -1;
	
	public StaticTile(int titleR, int subtitleR, int iconR, Context context, ViewGroup parent) {
		super(context, parent);
		this.titleR = titleR;
		this.subtitleR = subtitleR;
		this.iconR = iconR;
	}

	@Override
	public void fill(Object object) {
		setAlpha(1.0);

		this.titleLabel.setText(titleR);
		if(subtitleR == hideResource) {
			this.subtitleLabel.setVisibility(View.GONE);
		}
		else {
			this.subtitleLabel.setText(subtitleR);
			this.subtitleLabel.setVisibility(View.VISIBLE);
		}
		if(iconR == hideResource) {
			this.icon.setVisibility(View.INVISIBLE);
		}
		else {
			this.icon.setImageResource(iconR);
			this.icon.setVisibility(View.VISIBLE);
		}
		this.attributeIcon.setVisibility(View.INVISIBLE);
		this.timeLabel.setVisibility(View.GONE);
	}

}
