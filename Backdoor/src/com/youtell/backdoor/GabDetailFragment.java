package com.youtell.backdoor;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.youtell.backdoor.dummy.DummyContent;

/**
 * A fragment representing a single Gab detail screen.
 */
public class GabDetailFragment extends ListFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GabDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments().containsKey(ARG_ITEM_ID)) {
        	// Load the dummy content specified by the fragment
        	// arguments. In a real-world scenario, use a Loader
        	// to load content from a content provider.
        	mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
        
        if(mItem != null)
        	setListAdapter(new GabDetailMessageAdapter(getActivity(), 
        			mItem));
    }

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View view = super.onCreateView(inflater, container, savedInstanceState);
    	ListView listView = (ListView) view.findViewById(android.R.id.list);
    	listView.setDivider(null);
    	listView.setDividerHeight(0);
    	return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
