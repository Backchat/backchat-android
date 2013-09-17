package com.youtell.backdoor.adapters;

import java.util.List;

import com.youtell.backdoor.Tile;
import com.youtell.backdoor.models.Friend;

import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

public class FriendListAdapter extends ORMListAdapter<Friend> {
	public FriendListAdapter(Context context) {
		super(context);
		updateData();
	}	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Friend friend = (Friend) this.getItem(position);
		 
		Tile tile; 
		if(convertView == null)
		{
			tile = new Tile(context, parent);
			convertView = tile.getViews();
			convertView.setTag(tile);
		}
		else
			tile = (Tile) convertView.getTag();
	 		
		tile.fillWithFriend(friend);
		
		return convertView;
	}

	@Override
	protected List<Friend> getList() {
		return Friend.all();
	}

}
