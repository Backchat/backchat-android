package com.youtell.backdoor.fragments;

import com.youtell.backdoor.adapters.FriendListAdapter;
import com.youtell.backdoor.dummy.DummyContent;
import com.youtell.backdoor.models.Friend;

public class NewGabFragment extends ListAdapterCallbackFragment<FriendListAdapter, Friend> {
	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Friend> {}
	
	@Override
	protected FriendListAdapter createAdapter() {
		return new FriendListAdapter(getActivity(), DummyContent.FRIENDS);
	}
}
