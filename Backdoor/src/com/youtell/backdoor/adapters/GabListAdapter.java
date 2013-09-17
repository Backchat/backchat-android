package com.youtell.backdoor.adapters;

import java.util.List;

import com.youtell.backdoor.Tile;
import com.youtell.backdoor.models.Gab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class GabListAdapter extends ORMListAdapter<Gab> {
	public GabListAdapter(Context context) {
		super(context);
		updateData();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Gab gab = (Gab) this.getItem(position);
		 
		Tile tile; 
		if(convertView == null)
		{
			tile = new Tile(context, parent);
			convertView = tile.getViews();
			convertView.setTag(tile);
		}
		else
			tile = (Tile) convertView.getTag();
	 		
		tile.fillWithGab(gab);
		
		return convertView;
	}

	@Override
	protected List<Gab> getList() {
		return Gab.all();
	}
}
