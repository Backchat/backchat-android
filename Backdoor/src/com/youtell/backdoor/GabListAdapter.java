package com.youtell.backdoor;

import java.util.List;

import com.youtell.backdoor.dummy.DummyContent.DummyItem;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class GabListAdapter extends BaseAdapter {
	private class ViewHolder {
		TextView message;
	}

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
	 
		/*LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
		//Check whether message is mine to show green background and align to right
		if(true) {
			//holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
			lp.gravity = Gravity.RIGHT;
		}
		else
		{
			//holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
			lp.gravity = Gravity.LEFT;
		}
		holder.message.setLayoutParams(lp);
		//holder.message.setTextColor(R.color.textColor);	
		*/
		
		return convertView;
	}

	
}
