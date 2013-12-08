package com.youtell.backchat.adapters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.database.DataSetObserver;

public class MultipleListAdapter extends BaseAdapter  { 
	public final ArrayList<Adapter> sections = new ArrayList<Adapter>();  
	private final static String TAG = "MultipleListAdapter";
	
	public MultipleListAdapter() {
		super();
	}  

	public void addSection(Adapter adapter) {  
		this.sections.add(adapter);
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				Log.i(TAG, "onChanged");
				MultipleListAdapter.this.notifyDataSetChanged();
			}
		});
	}  

	public Object getItem(int position) {  
		for(Adapter adapter : this.sections) {
			int size = adapter.getCount();
			// check if position inside this section   
			if(position < size) return adapter.getItem(position);

			// otherwise jump into next section  
			position -= size;  
		}  
		return null;  
	}  

	public int getCount() {  
		// total together all sections
		int total = 0;  
		for(Adapter adapter : this.sections) {
			total += adapter.getCount();
		}
		return total;  
	}  

	public int getViewTypeCount() {  
		int total = 0;  
		for(Adapter adapter : this.sections)
			total += adapter.getViewTypeCount();  
		return total;  
	}  

	public int getItemViewType(int position) {  
		int type = 0;  
		for(Adapter adapter : this.sections) {
			int size = adapter.getCount();

			// check if position inside this section   
			if(position < size) return type + adapter.getItemViewType(position);

			// otherwise jump into next section  
			position -= size;  
			type += adapter.getViewTypeCount();  
		}  
		return -1;  
	}  

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {  
		for(Adapter adapter : this.sections) { 
			int size = adapter.getCount();

			// check if position inside this section   
			if(position < size) return adapter.getView(position, convertView, parent);  

			// otherwise jump into next section  
			position -= size;  
		}  
		return null;  
	}  

	@Override  
	public long getItemId(int position) {  
		return position;  
	}  
	
	public Adapter getAdapterForItem(int position) {
		for(Adapter adapter : this.sections) {
			int size = adapter.getCount();

			// check if position inside this section   
			if(position < size) return adapter;

			// otherwise jump into next section  
			position -= size;  
		}  
		
		return null;
	}
}  
