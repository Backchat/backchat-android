package com.youtell.backdoor.fragments;

import java.util.Date;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.youtell.backdoor.R;
import com.youtell.backdoor.adapters.GabDetailMessageAdapter;
import com.youtell.backdoor.dummy.DummyContent;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;

/**
 * A fragment representing a single Gab detail screen.
 */
public class GabDetailFragment extends ListFragment implements OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_GAB_ID = "gab_id";

	public static final String FROM_MESSAGE_RES = "FROM_MESSAGE_RES";
	public static final String TO_MESSAGE_RES = "TO_MESSAGE_RES";
    
	private EditText textInput;
	private Gab gab;
	
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
        gab = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_GAB_ID));

        int fromMessageRes = getArguments().getInt(FROM_MESSAGE_RES);
        int toMessageRes = getArguments().getInt(TO_MESSAGE_RES);
        
        setListAdapter(new GabDetailMessageAdapter(getActivity(), 
        		gab, fromMessageRes, toMessageRes));
        
        if(gab.isEmpty()) {
        	//TODO make keyboard active
        }
    }

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View view = inflater.inflate(R.layout.fragment_gab_detail, container, false);
    	ListView listView = (ListView) view.findViewById(android.R.id.list);
    	listView.setDivider(null);
    	listView.setDividerHeight(0);
    	
    	textInput = (EditText) view.findViewById(R.id.gab_input_text);
    	final Button button = (Button) view.findViewById(R.id.gab_send_button);
    	button.setOnClickListener(this);
    	return view;
    }
    
    public void onClick(View v) {
    	if(v.getId() == R.id.gab_send_button) {
    		Message m = new Message(textInput.getText().toString(), true, new Date(), false);
    		gab.addMessage(m);
    	}
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
