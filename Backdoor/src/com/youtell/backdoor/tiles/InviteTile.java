package com.youtell.backdoor.tiles;

import com.youtell.backdoor.R;

import android.content.Context;
import android.view.ViewGroup;

public class InviteTile extends StaticTile {

	public InviteTile(Context context, ViewGroup parent) {
		super(R.string.invite_tile_title, R.string.invite_tile_subtitle, 
				R.drawable.invite_tile, context, parent);
	}

}
