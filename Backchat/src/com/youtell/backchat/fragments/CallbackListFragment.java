package com.youtell.backchat.fragments;

import android.app.Activity;
import android.app.ListFragment;


public class CallbackListFragment<T> extends ListFragment {
	protected T mCallbacks;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (T) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

}
