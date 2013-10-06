package com.youtell.backdoor.tiles;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class StaticTile extends Tile {
	private int titleR;
	private int subtitleR;
	
	public StaticTile(int titleR, int subtitleR, Context context, ViewGroup parent) {
		super(context, parent);
		this.titleR = titleR;
		this.subtitleR = subtitleR;
	}

	@Override
	public void fill(Object object) {
		this.titleLabel.setText(titleR);
		this.subtitleLabel.setText(subtitleR);
		this.attributeIcon.setVisibility(View.INVISIBLE);
		this.timeLabel.setVisibility(View.GONE);
	}

}
