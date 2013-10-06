package com.youtell.backdoor.adapters;

import java.util.List;

import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.tiles.FriendTile;
import com.youtell.backdoor.tiles.Tile;

import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

public class FriendListAdapter extends ORMListAdapter<Friend> {
	private int mode;
	
	public static final int FEATURED_MODE = 0;
	public static final int FRIENDS_MODE = 1;
	
	public FriendListAdapter(Context context, int mode) {
		super(context);
		this.mode = mode;
		updateData();
	}	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Friend friend = (Friend) this.getItem(position);
		 
		Tile tile; 
		if(convertView == null)
		{
			tile = new FriendTile(context, parent);
			convertView = tile.getViews();
			convertView.setTag(tile);
		}
		else
			tile = (Tile) convertView.getTag();
	 		
		tile.fill(friend);
		
		return convertView;
	}

	@Override
	protected List<Friend> getList() {
		if(mode == FEATURED_MODE)
			return Friend.allFeatured();
		else
			return Friend.allFriends();
	}

}
