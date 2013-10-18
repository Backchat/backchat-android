package com.youtell.backdoor.adapters;

import java.util.List;

import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.tiles.FriendTile;
import com.youtell.backdoor.tiles.MoreFriendsTile;
import com.youtell.backdoor.tiles.StaticTile;
import com.youtell.backdoor.tiles.Tile;

import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

public class FriendListAdapter extends ORMListAdapter<Friend> {
	private int mode;
	private double alpha;
	private int maxVisible;
	
	public static final int FEATURED_MODE = 0;
	public static final int FRIENDS_MODE = 1;
	public static final int ALL_VISIBLE = -1;
	
	public FriendListAdapter(Context context, int mode, double alpha) {
		super(context);
		this.mode = mode;
		this.alpha = alpha;
		maxVisible = ALL_VISIBLE;
		updateData();		
	}	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		Friend friend = (Friend) this.getItem(position);
		 
		FriendTile tile; 
		if(convertView == null)
		{
			tile = new FriendTile(context, parent);
			convertView = tile.getViews();
			convertView.setTag(tile);
		}
		else
			tile = (FriendTile) convertView.getTag();
	 			
		tile.setFriendAlpha(alpha);
		tile.fill(friend);
		
		return convertView;
	}
	
	@Override
	protected List<Friend> getList() {
		List<Friend> f;
		if(mode == FEATURED_MODE)
			f = Friend.allFeatured();
		else
			f = Friend.allFriends();
		
		if(f.size() > maxVisible && maxVisible != -1)
			f = f.subList(0, maxVisible);
		
		return f;
	}

	public void setVisibleCount(int maxFriendCount) {
		maxVisible = maxFriendCount;
	}
}
