package com.youtell.backchat.tiles;

import com.youtell.backchat.R;

import android.content.Context;
import android.view.ViewGroup;

public class MoreFriendsTile extends StaticTile {

	public MoreFriendsTile(Context context, ViewGroup parent) {
		super(R.string.more_friends_tile_title, StaticTile.hideResource,
				R.drawable.show_more_tile, context, parent);
	}

}
