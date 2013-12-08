package com.youtell.backchat.tiles;

import com.youtell.backchat.R;

import android.content.Context;
import android.view.ViewGroup;

public class BuyClueTile extends StaticTile {
	public BuyClueTile(Context context, ViewGroup parent) {
		super(R.string.clues_tile_title, R.string.clues_tile_subtitle, R.drawable.clues_tile, context, parent);
	}
}
