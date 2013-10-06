package com.youtell.backdoor.tiles;

import com.youtell.backdoor.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class BuyClueTile extends Tile {

	public BuyClueTile(Context context, ViewGroup parent) {
		super(context, parent);
	}

	@Override
	public void fill(Object object) {
		this.titleLabel.setText(R.string.clues_tile_title);
		this.subtitleLabel.setText(R.string.clues_tile_subtitle);
		this.attributeIcon.setVisibility(View.INVISIBLE);
		this.timeLabel.setVisibility(View.GONE);
	}
}
