package com.youtell.backdoor;

import java.util.List;

import com.youtell.backdoor.dummy.DummyContent.DummyItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GabListAdapter extends BaseAdapter {
	private List<DummyItem> items;
	private Context context;
	
	public GabListAdapter(Context context, List<DummyItem> items) {
		this.items = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DummyItem message = (DummyItem) this.getItem(position);
		 
		Tile tile; 
		if(convertView == null)
		{
			tile = new Tile(context, parent);
			convertView = tile.getViews();
			convertView.setTag(tile);
		}
		else
			tile = (Tile) convertView.getTag();
	 
		tile.fillWithGab(message);		
		
		return convertView;
	}

	
}
