package com.youtell.backdoor.tiles;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.youtell.backdoor.R;
import com.youtell.backdoor.R.drawable;
import com.youtell.backdoor.models.Friend;

public class FriendTile extends Tile {
	private double alpha;
	
	public FriendTile(Context context, ViewGroup parent) {
		super(context, parent);
	}
	
	public FriendTile(Context context, View existing) {
		super(context, existing);
	}

	@Override
	public void fill(Object object) {
		setAlpha(alpha);
		
		Friend friend = (Friend)object;
		this.titleLabel.setText(friend.getFullName());
		this.subtitleLabel.setVisibility(View.GONE);
		this.timeLabel.setVisibility(View.GONE);

		loadAvatar(friend.getAvatar());

		if(friend.isFeatured()) {
			this.attributeIcon.setVisibility(View.VISIBLE);
			this.attributeIcon.setBackgroundResource(R.drawable.featured_accessory);
		}
		else {
			this.attributeIcon.setVisibility(View.INVISIBLE);
		}
	}

	public void setFriendAlpha(double alpha) {
		this.alpha = alpha;		
	}
}
