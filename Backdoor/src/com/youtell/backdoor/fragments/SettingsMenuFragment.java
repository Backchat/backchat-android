package com.youtell.backdoor.fragments;

import com.youtell.backdoor.R;
import com.youtell.backdoor.R.array;
import com.youtell.backdoor.R.id;
import com.youtell.backdoor.R.layout;
import com.youtell.backdoor.R.string;

import android.app.ListFragment;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SettingsMenuFragment extends ListFragment {
	private TextView versionLabel;
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
    	View view = inflater.inflate(R.layout.fragment_settings_menu, container, false);
    	versionLabel = (TextView) view.findViewById(R.id.settings_menu_version_label);
		//ListView listView = (ListView) view.findViewById(android.R.id.list);
    	String versionName;
    	try {
			versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "???";
		}

    	versionLabel.setText(String.format(getResources().getString(R.string.version_string),  versionName));
    	
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
