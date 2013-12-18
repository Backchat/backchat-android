package com.youtell.backchat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youtell.backchat.R;
import com.youtell.backchat.adapters.FriendListAdapter;
import com.youtell.backchat.models.Friend;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.FriendObserver;
import com.youtell.backchat.observers.UserObserver;

public class NewGabFragment extends ListAdapterCallbackFragment<FriendListAdapter, FriendObserver, Friend, NewGabFragment.Callbacks>
{
	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Friend> {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_new_gab, container, false);
		return view;
	}
	
	@Override
	protected FriendListAdapter createAdapter() {
		return new FriendListAdapter(getActivity(), FriendListAdapter.FRIENDS_MODE, 1.0);
	}

	@Override
	protected FriendObserver createObserver() {
		return new FriendObserver(new FriendObserver.Observer() {
			
			@Override
			public void refresh() {
				onChange();
			}
			
			@Override
			public void onChange() {
				if(adapter != null)
					adapter.notifyDataSetChanged();				
			}
		});
	}

	@Override
	public void onResume() {
		User.getCurrentUser().getFriends();
		super.onResume();
	}
}
