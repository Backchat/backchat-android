package com.youtell.backdoor.fragments;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.youtell.backdoor.observers.LocalObserver;

public abstract class ListAdapterCallbackFragment<Adapter extends BaseAdapter, ModelObserverType extends LocalObserver<?>, 
CallbackType, CallbackT extends ListAdapterCallbackFragment.Callbacks<CallbackType>> 
extends ListFragment {
	protected CallbackT mCallbacks = null;

	protected interface Callbacks<CallbackType> {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(CallbackType item);
	}

	protected Adapter adapter;

	protected abstract Adapter createAdapter();
	protected abstract ModelObserverType createObserver();
	
	protected ModelObserverType observer;
		
	@Override
	public void onResume()
	{		
		super.onResume();
		observer.startListening();
	}
	
	@Override
	public void onStop()
	{
		observer.stopListening();
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		observer = createObserver();		
	}

	@Override 
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		setupAdapter();
		//TODO use multiple image loaders
		PauseOnScrollListener scrollPause = new PauseOnScrollListener(ImageLoader.getInstance(), false, true);
		getListView().setOnScrollListener(scrollPause);
	}

	protected void setupAdapter()
	{
		this.adapter = createAdapter();
		setListAdapter(this.adapter);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof Callbacks<?>)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (CallbackT) activity;
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
