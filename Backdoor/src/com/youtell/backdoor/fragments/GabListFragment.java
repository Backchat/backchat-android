package com.youtell.backdoor.fragments;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.youtell.backdoor.adapters.FriendListAdapter;
import com.youtell.backdoor.adapters.GabListAdapter;
import com.youtell.backdoor.adapters.ItemAdapter;
import com.youtell.backdoor.adapters.MultipleListAdapter;
import com.youtell.backdoor.api.GetGabsRequest;
import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.APIRequestObserver;
import com.youtell.backdoor.observers.FriendObserver;
import com.youtell.backdoor.observers.GabObserver;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.tiles.BuyClueTile;
import com.youtell.backdoor.tiles.InviteTile;
import com.youtell.backdoor.tiles.ShareTile;
import com.youtell.backdoor.tiles.Tile;

public class GabListFragment extends ListFragment
implements OnRefreshListener, APIRequestObserver.Observer<GetGabsRequest>, UserObserver.Observer {
	private APIRequestObserver<GetGabsRequest> gabsRequestObserver;
	private Callbacks mCallbacks = null;

	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onGabSelected(Gab gab);
		public void onFriendSelected(Friend f);
		public PullToRefreshAttacher getPullToRefreshAttacher();
		public void onBuyClue();
		public void onInvite();
	}

	private MultipleListAdapter adapter;
	private GabListAdapter gabListAdapter;
	private FriendListAdapter friendListAdapter;
	private FriendListAdapter featuredListAdapter;
	private ItemAdapter shareAdapter;
	private ItemAdapter buyClueAdapter;
	private ItemAdapter inviteAdapter;
	
	private GabObserver gabObserver;	
	private FriendObserver friendObserver;
	private Object userObserver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gabObserver = new GabObserver(new GabObserver.Observer() {			
			@Override
			public void onChange(String action, int gabID) {
				gabListAdapter.notifyDataSetChanged();
			}
		});

		friendObserver = new FriendObserver(new FriendObserver.Observer() {			
			@Override
			public void onChange() {
				friendListAdapter.notifyDataSetChanged();
				featuredListAdapter.notifyDataSetChanged();
			}
		});

		gabsRequestObserver = new APIRequestObserver<GetGabsRequest>(this, GetGabsRequest.class);
	}	

	@Override 
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		adapter = new MultipleListAdapter();

		gabListAdapter = new GabListAdapter(getActivity());
		friendListAdapter = new FriendListAdapter(getActivity(), FriendListAdapter.FRIENDS_MODE, 0.6);		
		featuredListAdapter = new FriendListAdapter(getActivity(), FriendListAdapter.FEATURED_MODE, 1.0);
		shareAdapter = new ItemAdapter(getActivity(), ShareTile.class);
		buyClueAdapter = new ItemAdapter(getActivity(), BuyClueTile.class);
		inviteAdapter = new ItemAdapter(getActivity(), InviteTile.class);
		
		adapter.addSection(gabListAdapter);
		adapter.addSection(friendListAdapter);
		adapter.addSection(featuredListAdapter);
		adapter.addSection(shareAdapter);		
		adapter.addSection(buyClueAdapter);
		adapter.addSection(inviteAdapter);

		setListAdapter(this.adapter);

		//TODO use multiple image loaders
		PauseOnScrollListener scrollPause = new PauseOnScrollListener(ImageLoader.getInstance(), false, true);
		getListView().setOnScrollListener(scrollPause);
		mCallbacks.getPullToRefreshAttacher().addRefreshableView(getListView(), this);
	}

	@Override
	public void onResume()
	{		
		super.onResume();
		gabObserver.startListening();
		friendObserver.startListening();
		userObserver = UserObserver.registerObserver(this);
		gabsRequestObserver.startListening();
	}

	@Override
	public void onStop()
	{
		UserObserver.unregisterObserver(userObserver);
		gabObserver.stopListening();
		friendObserver.stopListening();
		gabsRequestObserver.stopListening();
		super.onStop();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks)activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		Adapter a = adapter.getAdapterForItem(position);
		Object obj = adapter.getItem(position);
		if(a == gabListAdapter)
			mCallbacks.onGabSelected((Gab)obj);
		else if(a == friendListAdapter || a == featuredListAdapter)
			mCallbacks.onFriendSelected((Friend)obj);
		else if(a == buyClueAdapter) 
			mCallbacks.onBuyClue();
		else if(a == inviteAdapter)
			mCallbacks.onInvite();
	}		

	@Override
	public void onUserChanged() {		
	}

	@Override
	public void onUserSwapped(User old, User newUser) {
		updateData(newUser);
	}

	@Override
	public void onRefreshStarted(View view) {
		updateData(user);
	}

	@Override
	public void onSuccess() {
		if(mCallbacks != null)		
			mCallbacks.getPullToRefreshAttacher().setRefreshComplete();
	}

	@Override
	public void onFailure() {
		if(mCallbacks != null)
			mCallbacks.getPullToRefreshAttacher().setRefreshComplete();
	}

	private User user;

	private void updateData(User user) {
		this.user = user;
		if(this.user != null) {
			user.updateGabs();
			user.getFriends();
		}
	}


}
