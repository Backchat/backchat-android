package com.youtell.backdoor.adapters;

import java.util.List;

import com.youtell.backdoor.Tile;
import com.youtell.backdoor.models.Friend;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;

public class FriendListAdapter extends BaseAdapter {
	private Context context;
	private List<Friend> friends;
	
	public FriendListAdapter(Context context, List<Friend> f) {
		this.context = context;
		this.friends = f;
	}

	@Override
	public int getCount() {
		return friends.size();
	}

	@Override
	public Object getItem(int position) {
		return friends.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
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

}
