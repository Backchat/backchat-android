package com.youtell.backdoor.adapters;


import com.youtell.backdoor.tiles.Tile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ItemAdapter extends BaseAdapter {
	public interface Factory {
		public Tile newTile(Context context, ViewGroup parent);
	}
	
	private Context context;
	private Factory factory;
	
	public ItemAdapter(Context context, Factory f) {
		super();
		this.factory = f;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tile tile; 
		if(convertView == null)
		{
			tile = factory.newTile(context, parent);
			convertView = tile.getViews();
			convertView.setTag(tile);
		}
		else
			tile = (Tile) convertView.getTag();
	 		
		tile.fill(null);
		
		return convertView;
	}

}
