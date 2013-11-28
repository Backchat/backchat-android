package com.youtell.backdoor.fragments;

import com.youtell.backdoor.adapters.FriendListAdapter;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.FriendObserver;
import com.youtell.backdoor.observers.UserObserver;

public class NewGabFragment extends ListAdapterCallbackFragment<FriendListAdapter, FriendObserver, Friend, NewGabFragment.Callbacks>
{
	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Friend> {}
	
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
