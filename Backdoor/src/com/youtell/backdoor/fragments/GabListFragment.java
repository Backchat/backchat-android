package com.youtell.backdoor.fragments;

import android.os.Bundle;

import com.youtell.backdoor.adapters.GabListAdapter;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.GabListObserver;

public class GabListFragment extends ListAdapterCallbackFragment<GabListAdapter, GabListObserver, Gab, GabListFragment.Callbacks> 
implements GabListObserver.Observer {
	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Gab> {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}	
	
	@Override
	protected GabListAdapter createAdapter() {
		return new GabListAdapter(getActivity());
	}

	@Override
	protected GabListObserver createObserver() {
		return new GabListObserver(this);
	}

	@Override
	public void onChange() {
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	protected void refreshData() {
		new User().updateGabs(); //TODO	
	}

	
}
