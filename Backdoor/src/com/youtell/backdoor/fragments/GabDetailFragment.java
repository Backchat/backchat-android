package com.youtell.backdoor.fragments;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.youtell.backdoor.R;
import com.youtell.backdoor.adapters.GabDetailMessageAdapter;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.observers.MessageListObserver;

/**
 * A fragment representing a single Gab detail screen.
 */
public class GabDetailFragment extends ListAdapterCallbackFragment<GabDetailMessageAdapter, MessageListObserver, Message, GabDetailFragment.Callbacks> 
implements OnClickListener, MessageListObserver.Observer {
    public static final String ARG_GAB_ID = "gab_id";

	public static final String FROM_MESSAGE_RES = "FROM_MESSAGE_RES";
	public static final String TO_MESSAGE_RES = "TO_MESSAGE_RES";
    
	private EditText textInput;
	private Gab gab;
	
	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Message> {
		public void beforeMessageSend(Message message);
	}
	
    public GabDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        gab = Gab.getByID(getArguments().getInt(ARG_GAB_ID, -1)); //TODO
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
       
        setupAdapter();
        
    	return view;
    }
        
    public void onClick(View v) {
    	if(v.getId() == R.id.gab_send_button) {
    		Message m = new Message();
    		m.setText(textInput.getText().toString());
    		m.setMine(true);
    		m.setCreatedAt(new Date());
    		m.setSent(false);

    		
    		if(mCallbacks != null) {
    			mCallbacks.beforeMessageSend(m);
    		}
    		
    		gab.addMessage(m);
    		
    		textInput.setText(null);
    	}
    }
       
	@Override
	public void onPause() {
		//TODO try to keep it open if it is open? causes the activity to black out for some reason (too slow?)
	    InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		super.onPause();
	}
	
	@Override
	public void onChange(String action, int gabID, int messageID) {
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	protected GabDetailMessageAdapter createAdapter() {
		int fromMessageRes = getArguments().getInt(FROM_MESSAGE_RES);
        int toMessageRes = getArguments().getInt(TO_MESSAGE_RES);
        return new GabDetailMessageAdapter(getActivity(), 
    			gab, fromMessageRes, toMessageRes);
	}

	@Override
	protected MessageListObserver createObserver() {
		return new MessageListObserver(this, gab);
	}
	
	@Override
	protected void refreshData() {
	
	}

}
