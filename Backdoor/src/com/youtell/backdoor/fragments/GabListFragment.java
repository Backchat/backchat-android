package com.youtell.backdoor.fragments;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.os.Bundle;
import android.view.View;

import com.youtell.backdoor.adapters.GabListAdapter;
import com.youtell.backdoor.api.GetGabsRequest;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.APIRequestObserver;
import com.youtell.backdoor.observers.GabObserver;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.observers.UserObserver.Observer;

public class GabListFragment extends ListAdapterCallbackFragment<GabListAdapter, GabObserver, Gab, GabListFragment.Callbacks> 
implements GabObserver.Observer, OnRefreshListener, APIRequestObserver.Observer<GetGabsRequest>, Observer {
	private APIRequestObserver<GetGabsRequest> gabsRequestObserver;
	
	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Gab> {
		public PullToRefreshAttacher getPullToRefreshAttacher();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gabsRequestObserver = new APIRequestObserver<GetGabsRequest>(this, GetGabsRequest.class);
	}	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		mCallbacks.getPullToRefreshAttacher().addRefreshableView(getListView(), this);
	}
	
	@Override
	protected GabListAdapter createAdapter() {
		return new GabListAdapter(getActivity());
	}

	@Override
	protected GabObserver createObserver() {
		return new GabObserver(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		gabsRequestObserver.startListening();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		gabsRequestObserver.stopListening();
	}
	
	@Override
	public void onChange(String action, int gabID) {
		if(adapter != null)
			adapter.notifyDataSetChanged();		
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
	
	@Override
	protected void updateData(User user) {
		this.user = user;
		if(this.user != null) {
			user.updateGabs();
		}
	}
	
}
