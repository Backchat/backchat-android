package com.youtell.backchat.fragments;

import java.util.Locale;

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
import com.youtell.backchat.Application;
import com.youtell.backchat.Settings;
import com.youtell.backchat.adapters.FriendListAdapter;
import com.youtell.backchat.adapters.GabListAdapter;
import com.youtell.backchat.adapters.ItemAdapter;
import com.youtell.backchat.adapters.MultipleListAdapter;
import com.youtell.backchat.api.GetGabsRequest;
import com.youtell.backchat.models.Friend;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.APIRequestObserver;
import com.youtell.backchat.observers.FriendObserver;
import com.youtell.backchat.observers.GabObserver;
import com.youtell.backchat.observers.UserObserver;
import com.youtell.backchat.tiles.BuyClueTile;
import com.youtell.backchat.tiles.InviteTile;
import com.youtell.backchat.tiles.MoreFriendsTile;
import com.youtell.backchat.tiles.ShareTile;
import com.youtell.backchat.tiles.Tile;

public class GabListFragment extends ListFragment
implements OnRefreshListener {
	private APIRequestObserver<GetGabsRequest> gabsRequestObserver;
	private Callbacks mCallbacks = null;

	private static final int MAX_FRIEND_COUNT = 3;
	
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onGabSelected(Gab gab);
		public void onFriendSelected(Friend f);
		public PullToRefreshAttacher getPullToRefreshAttacher();
		public void onBuyClue();
		public void onInvite();
		public void onShareApp();
		public void onMoreFriends();
	}

	private MultipleListAdapter adapter;
	private GabListAdapter gabListAdapter;
	private FriendListAdapter friendListAdapter;
	private FriendListAdapter featuredListAdapter;
	private ItemAdapter shareAdapter;
	private ItemAdapter buyClueAdapter;
	private ItemAdapter moreFriendsAdapter;
	private ItemAdapter inviteAdapter;
	
	private GabObserver gabObserver;	
	private FriendObserver friendObserver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gabObserver = new GabObserver(new GabObserver.Observer() {			
			@Override
			public void onChange(String action, int gabID) {
				if(action == GabObserver.GAB_BATCH_UPDATED)
					gabListAdapter.notifyDataSetChanged();
			}

			@Override
			public void refresh() {
				gabListAdapter.notifyDataSetChanged();				
			}
		});

		friendObserver = new FriendObserver(new FriendObserver.Observer() {			
			@Override
			public void onChange() {
				friendListAdapter.notifyDataSetChanged();
				featuredListAdapter.notifyDataSetChanged();
				
				int fcount = Friend.allFriends().size();
				if(fcount > MAX_FRIEND_COUNT) {
					friendListAdapter.setVisibleCount(MAX_FRIEND_COUNT);
					moreFriendsAdapter.setVisible(true);
				}
				else {
					friendListAdapter.setVisibleCount(FriendListAdapter.ALL_VISIBLE);
					moreFriendsAdapter.setVisible(false);
				}
			}

			@Override
			public void refresh() {
				onChange();				
			}
		});

		gabsRequestObserver = new APIRequestObserver<GetGabsRequest>(new APIRequestObserver.Observer<GetGabsRequest>() {
			@Override
			public void onSuccess(GetGabsRequest r) {
				if(mCallbacks != null)		
					mCallbacks.getPullToRefreshAttacher().setRefreshComplete();
			}

			@Override
			public void onFailure(GetGabsRequest r) {
				if(mCallbacks != null)
					mCallbacks.getPullToRefreshAttacher().setRefreshComplete();
			}
			
		}, GetGabsRequest.class);
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
		moreFriendsAdapter = new ItemAdapter(getActivity(), MoreFriendsTile.class);
		moreFriendsAdapter.setVisible(false);
		
		boolean shouldShowFeatured = false;
		if(Locale.getDefault().getISO3Country().equalsIgnoreCase("BRA") || Settings.settings.alwaysShowFeatured)
			shouldShowFeatured = true;
		
		if(shouldShowFeatured)
			adapter.addSection(featuredListAdapter);
		
		buyClueAdapter.setVisible(false);
		
		adapter.addSection(gabListAdapter);
		adapter.addSection(friendListAdapter);
		adapter.addSection(moreFriendsAdapter);		
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
		gabsRequestObserver.startListening();

		updateUserData();
	}

	public void pauseListening() {
		gabObserver.stopListening();
		friendObserver.stopListening();
		gabsRequestObserver.stopListening();		
	}
	
	@Override
	public void onStop()
	{
		pauseListening();
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
		if(a == gabListAdapter) {
			Application.mixpanel.track("Tapped Main View / Gab Item", null);
			mCallbacks.onGabSelected((Gab)obj);
		}
		else if(a == friendListAdapter) {
			Application.mixpanel.track("Tapped Main View / Friend Item", null);
			mCallbacks.onFriendSelected((Friend)obj);
			
		}
		else if(a == featuredListAdapter) {
			Application.mixpanel.track("Tapped Main View / Featured Users Item", null);
			mCallbacks.onFriendSelected((Friend)obj);
		}
		else if(a == buyClueAdapter) {
			Application.mixpanel.track("Tapped Main View / Clues Item", null);
			mCallbacks.onBuyClue();
		}
		else if(a == inviteAdapter) {
			Application.mixpanel.track("Tapped Main View / Invite Friends", null);
			mCallbacks.onInvite();
		}
		else if(a == shareAdapter) {
			Application.mixpanel.track("Tapped Main View / Share Item", null);
			mCallbacks.onShareApp();
		}
		else if(a == moreFriendsAdapter) {
			Application.mixpanel.track("Tapped Main View / More Item", null);
			mCallbacks.onMoreFriends();
		}
	}		
	
	@Override
	public void onRefreshStarted(View view) {
		updateUserData();
	}

	
	private void updateUserData() {
		User user = User.getCurrentUser();
		user.updateGabs();
		user.getFriends();		
	}

	public void enableBuyClue() {
		buyClueAdapter.setVisible(true);
	}


}
