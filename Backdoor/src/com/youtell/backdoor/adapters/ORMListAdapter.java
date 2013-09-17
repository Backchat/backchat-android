package com.youtell.backdoor.adapters;

import java.util.List;

import com.youtell.backdoor.models.DatabaseObject;
import android.content.Context;
import android.widget.BaseAdapter;

public abstract class ORMListAdapter<T extends DatabaseObject> extends BaseAdapter {	
	private List<T> items;
	protected Context context;
	
	public ORMListAdapter(Context context) {
		this.context = context;
	}
	
	protected abstract List<T> getList();
	
	protected void updateData() {
		items = getList();
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		updateData();
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	protected T itemAt(int position) {
		return this.items.get(position);
	}
	
	@Override
	public Object getItem(int position) {
		return itemAt(position);
	}

	@Override
	public long getItemId(int position) {
		return itemAt(position).getID();
	}
}
