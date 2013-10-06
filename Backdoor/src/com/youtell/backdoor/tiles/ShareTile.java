package com.youtell.backdoor.tiles;

import com.youtell.backdoor.R;
import com.youtell.backdoor.R.string;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ShareTile extends Tile {

	public ShareTile(Context context, ViewGroup parent) {
		super(context, parent);
	}

	@Override
	public void fill(Object object) {
		this.titleLabel.setText(R.string.share_tile_title);
		this.subtitleLabel.setText(R.string.share_tile_subtitle);
		this.attributeIcon.setVisibility(View.INVISIBLE);
		//TODO add icon		
		this.timeLabel.setVisibility(View.GONE);

	}
}