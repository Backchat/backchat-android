package com.youtell.backdoor.tiles;

import android.content.Context;
import android.view.ViewGroup;

import com.youtell.backdoor.R;

public class ShareTile extends StaticTile {

	public ShareTile(Context context, ViewGroup parent) {
		super(R.string.share_tile_title, R.string.share_tile_subtitle, 
				R.drawable.share_tile, context, parent);
	}
}