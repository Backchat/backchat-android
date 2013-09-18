package com.youtell.backdoor.fragments;

import android.app.Activity;
import android.app.Fragment;

public class CallbackFragment<T> extends Fragment {
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
