package com.youtell.backdoor;

import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SettingsMenuFragment extends ListFragment {
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		//ListView listView = (ListView) view.findViewById(android.R.id.list);
		return view;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        String[] settings = res.getStringArray(R.array.settings_menu_values);
        
        setListAdapter(new ArrayAdapter<String>(getActivity(),
        		android.R.layout.simple_list_item_1, settings));
    }
}
