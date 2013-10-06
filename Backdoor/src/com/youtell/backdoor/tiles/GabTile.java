package com.youtell.backdoor.tiles;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.youtell.backdoor.R;
import com.youtell.backdoor.Util;
import com.youtell.backdoor.R.drawable;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;

public class GabTile extends Tile {
	public GabTile(Context context, ViewGroup group) {
		super(context, group);
	}
	
	@Override
	public void fill(Object obj) {
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
