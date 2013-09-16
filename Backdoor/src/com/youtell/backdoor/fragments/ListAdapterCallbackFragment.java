package com.youtell.backdoor.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ListAdapterCallbackFragment<Adapter extends BaseAdapter, CallbackType> extends ListFragment {
	protected Callbacks<CallbackType> mCallbacks = null;

	protected interface Callbacks<CallbackType> {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(CallbackType item);
	}
	
	private Adapter adapter;

	protected abstract Adapter createAdapter();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		this.adapter = createAdapter();
		setListAdapter(adapter);
		return view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks<?>)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks<CallbackType>) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		if(mCallbacks != null)
			mCallbacks.onItemSelected((CallbackType)adapter.getItem(position));
	}
}
