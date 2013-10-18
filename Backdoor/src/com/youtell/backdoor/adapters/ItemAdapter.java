package com.youtell.backdoor.adapters;


import com.youtell.backdoor.tiles.Tile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ItemAdapter extends BaseAdapter {	
	private Context context;
	private Class<?> clazz;
	private boolean visible;

	public <T extends Tile> ItemAdapter(Context context, Class<T> clazz) {
		super();
		this.clazz = clazz;
		this.context = context;
		this.visible = true;
	}

	@Override
	public int getCount() {
		if(this.visible)
			return 1;
		else
			return 0;
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
			try {
				tile = (Tile)clazz.getConstructor(Context.class, ViewGroup.class).newInstance(context, parent);
				convertView = tile.getViews();
				convertView.setTag(tile);
			}
			catch(Exception e) {
				return null;
			}
		}
		else
			tile = (Tile) convertView.getTag();

		tile.fill(null);

		return convertView;
	}

	public void setVisible(boolean b) {
		visible = b;
		this.notifyDataSetChanged();
	}

}
