package com.youtell.backchat.fragments;

import com.youtell.backchat.Application;
import com.youtell.backchat.Util;
import com.youtell.backchat.social.SocialProvider;
import com.youtell.backchat.R;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsMenuFragment extends CallbackListFragment<SettingsMenuFragment.Callbacks> {
	private TextView versionLabel;
	private String[] settings;

	public interface Callbacks {
		public void onLogout();
		public void onShareApp();
		public void onChangeNotificationSettings();
		public void onReportAbuse();
		public void onAboutUs();
		public void onPrivacyLegal();
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_settings_menu, container, false);
		versionLabel = (TextView) view.findViewById(R.id.settings_menu_version_label);
		//ListView listView = (ListView) view.findViewById(android.R.id.list);

		Resources res = getResources();
		settings = res.getStringArray(R.array.settings_menu_values);

		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, settings));	

		versionLabel.setText(String.format(getResources().getString(R.string.version_string),  Util.getVersionName(getActivity())));

		return view;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		if(mCallbacks == null)
			return;

		switch(position) {
		case 0:
			Application.mixpanel.track("Tapped Share With Friends (Free Clues) Settings Button", null);
			mCallbacks.onShareApp();
			break;
		case 1:
			mCallbacks.onChangeNotificationSettings();
			break;
		case 2:
			Application.mixpanel.track("Tapped Report Abuse Button", null);
			mCallbacks.onReportAbuse();
			break;
		case 3:
			mCallbacks.onPrivacyLegal();
			break;
		case 4:
			mCallbacks.onAboutUs();
			break;
		case 5:
			Application.mixpanel.track("Logged out", null);
			mCallbacks.onLogout();
			break;
		default:
		}
	}
}
