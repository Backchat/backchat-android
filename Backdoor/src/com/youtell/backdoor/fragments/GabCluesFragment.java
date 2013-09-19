package com.youtell.backdoor.fragments;

import com.youtell.backdoor.R;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.UserObserver;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.TextView;

public class GabCluesFragment extends CallbackFragment<GabCluesFragment.Callbacks> 
implements UserObserver.Observer {
	public interface Callbacks {
		public void onCancel();
	}

	public static final String ARG_GAB_ID = "ARG_GAB_ID";
	private Gab gab;
	private GridLayout clueGrid;
	private TextView clueLabel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		gab = Gab.getByID(getArguments().getInt(ARG_GAB_ID, -1)); //TODO
		super.onCreate(savedInstanceState); 
	}       

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_gab_clues, container, false);

		clueGrid = (GridLayout)view.findViewById(R.id.gab_clues_grid);

		int x=0;
		int y=0;
		for(int i=0;i<gab.getClueCount();i++) {
			GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
			lp.height = LayoutParams.WRAP_CONTENT;
			lp.width = LayoutParams.WRAP_CONTENT;
			lp.columnSpec = GridLayout.spec(y);
			lp.rowSpec = GridLayout.spec(x);

			View clueItem = inflater.inflate(R.layout.gab_clues_clue_button, null, false);

			final Button clueButton = (Button) clueItem.findViewById(R.id.gab_clues_clue_icon_button);
			clueButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}

			});
			
			clueGrid.addView(clueItem, lp);
			
			x++;
			if(x > 2) {x = 0; y++;}
		}

		clueLabel = (TextView)view.findViewById(R.id.gab_clues_status_label);
		final Button cancelButton = (Button)view.findViewById(R.id.gab_clues_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mCallbacks != null)
					mCallbacks.onCancel();
			}

		});

		final Button moreButton = (Button)view.findViewById(R.id.gab_clues_more);
		moreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO IAP
			}

		});

		return view;
	}

	private Object userObserver;
	
	@Override
	public void onResume() {
		super.onResume();
		userObserver = UserObserver.registerObserver(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		UserObserver.unregisterObserver(userObserver);
	}

	private User user;
	
	private void updateClueCount()
	{
		clueLabel.setText(String.format(getActivity().getResources().getString(R.string.gab_clue_status_text), 
				user.getTotalClueCount()));
	}

	@Override
	public void onUserChanged() {
		updateClueCount();		
	}

	@Override
	public void onUserSwapped(User old, User newUser) {
		user = newUser;
		updateClueCount();
	}
}
