package com.youtell.backdoor;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.youtell.backdoor.adapters.GabDetailMessageAdapter;
import com.youtell.backdoor.dummy.DummyContent;
import com.youtell.backdoor.models.Gab;

/**
 * A fragment representing a single Gab detail screen.
 */
public class GabDetailFragment extends ListFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_GAB_ID = "gab_id";

	public static final String FROM_MESSAGE_RES = "FROM_MESSAGE_RES";
	public static final String TO_MESSAGE_RES = "TO_MESSAGE_RES";
    
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GabDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the dummy content specified by the fragment
        // arguments. In a real-world scenario, use a Loader
        // to load content from a content provider.
        Gab gab = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_GAB_ID));

        int fromMessageRes = getArguments().getInt(FROM_MESSAGE_RES);
        int toMessageRes = getArguments().getInt(TO_MESSAGE_RES);
        
        if(gab != null) {
        	setListAdapter(new GabDetailMessageAdapter(getActivity(), 
        			gab, fromMessageRes, toMessageRes));
        }
    }

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View view = inflater.inflate(R.layout.fragment_gab_detail, container, false);
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
