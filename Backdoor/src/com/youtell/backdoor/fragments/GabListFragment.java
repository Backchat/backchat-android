package com.youtell.backdoor.fragments;

import android.os.Bundle;

import com.youtell.backdoor.adapters.GabListAdapter;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.GabObserver;

public class GabListFragment extends ListAdapterCallbackFragment<GabListAdapter, GabObserver, Gab, GabListFragment.Callbacks> 
implements GabObserver.Observer {
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
	protected GabObserver createObserver() {
		return new GabObserver(this);
	}
	
	@Override
	protected void refreshData() {
		new User().updateGabs(); //TODO	
	}

	@Override
	public void onChange(String action, int gabID) {
		if(adapter != null)
			adapter.notifyDataSetChanged();		
	}
	
}
