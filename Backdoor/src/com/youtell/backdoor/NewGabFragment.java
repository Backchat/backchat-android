package com.youtell.backdoor;

import com.youtell.backdoor.adapters.FriendListAdapter;
import com.youtell.backdoor.dummy.DummyContent;

import android.app.ListFragment;
import android.os.Bundle;

public class NewGabFragment extends ListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new FriendListAdapter(getActivity(), 
				DummyContent.FRIENDS));
	}
}
