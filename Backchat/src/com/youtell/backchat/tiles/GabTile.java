package com.youtell.backchat.tiles;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.youtell.backchat.Util;
import com.youtell.backchat.models.Friend;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.R;
import com.youtell.backchat.R.drawable;

public class GabTile extends Tile {
	public GabTile(Context context, ViewGroup group) {
		super(context, group);
	}
	
	@Override
	public void fill(Object obj) {
		setAlpha(1.0);

		Gab gab = (Gab)obj;
		this.titleLabel.setText(gab.getTitle());
		this.subtitleLabel.setText(gab.getContentSummary());
		this.timeLabel.setVisibility(View.VISIBLE);
		this.timeLabel.setText(Util.humanDateTime(context, gab.getUpdatedAt()));
		if(gab.isUnread()) {
			this.attributeIcon.setVisibility(View.VISIBLE);
			this.attributeIcon.setBackgroundResource(R.drawable.read_accessory);
		}
		else {
			this.attributeIcon.setVisibility(View.INVISIBLE);
		}

		loadAvatar(gab.getRelatedAvatar());
	}
}
