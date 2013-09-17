package com.youtell.backdoor.fragments;

import com.youtell.backdoor.adapters.FriendListAdapter;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.FriendListObserver;

public class NewGabFragment extends ListAdapterCallbackFragment<FriendListAdapter, FriendListObserver, Friend, NewGabFragment.Callbacks>
implements FriendListObserver.Observer {
	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Friend> {}
	
	@Override
	protected FriendListAdapter createAdapter() {
		return new FriendListAdapter(getActivity());
	}

	@Override
	protected FriendListObserver createObserver() {
		return new FriendListObserver(this);
	}

	@Override
	public void onChange() {
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	protected void refreshData() {
		new User().getFriends();
	}
}
