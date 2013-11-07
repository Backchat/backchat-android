package com.youtell.backdoor.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.ForeignCollection;
import com.youtell.backdoor.ClueGridItem;
import com.youtell.backdoor.R;
import com.youtell.backdoor.models.Clue;
import com.youtell.backdoor.models.DatabaseObject;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.ClueObserver;
import com.youtell.backdoor.observers.UserObserver;

public class GabCluesFragment extends DialogFragment
implements UserObserver.Observer, ClueObserver.Observer {
	public interface Callbacks {
		public void onCancel();
		public void onBuy();
	}

	public static final String ARG_GAB_ID = "ARG_GAB_ID";
	private Gab gab;
	private GridLayout clueGrid;
	private TextView clueLabel;
	private ClueObserver clueObserver;
	private UserObserver userObserver = new UserObserver(this);
	private Callbacks mCallbacks;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	Dialog d = new Dialog(getActivity(), android.R.style.Theme);
    	d.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    	d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return d;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		gab = Gab.getByID(getArguments().getInt(ARG_GAB_ID, -1)); //TODO
		super.onCreate(savedInstanceState);
		clueObserver = new ClueObserver(this, gab);
		
	}       

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_gab_clues, container, false);

		clueGrid = (GridLayout)view.findViewById(R.id.gab_clues_grid);

		for(int i=0;i<gab.getClueCount();i++) {
			final int number = i;
			final ClueGridItem clueTile = new ClueGridItem(inflater, clueGrid, i, 3, 3, new ClueGridItem.Callback() {
				
				@Override
				public void onClick(ClueGridItem which) {
					which.startProgress();
					Clue c = new Clue();
					c.setRemoteID(DatabaseObject.NEW_OBJECT);
					c.setNumber(number);
					gab.addClue(c);					
				}
			});
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
				if(mCallbacks != null)
					mCallbacks.onBuy();
			}

		});

		updateClueGrid();
		return view;
	}

	private void updateClueGrid() {
		ForeignCollection<Clue> clues = gab.getClues();
		ArrayList<Clue> clueList = new ArrayList<Clue>(clues);
		for(int i=0;i<clueList.size();i++) {
			updateClue(clueList.get(i));
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		userObserver.startListening();
		clueObserver.startListening();
		updateClueCount();
		gab.updateClues(); //in case of change
	}

	@Override
	public void onPause() {
		super.onPause();
		clueObserver.stopListening();
		userObserver.stopListening();
	}
	
	private void updateClueCount()
	{
		if(User.getCurrentUser().getTotalClueCount() == User.UNKNOWN_CLUE_COUNT) {
			clueLabel.setText(R.string.gab_clue_updating_remaining);
		}
		else {
			clueLabel.setText(String.format(getActivity().getResources().getString(R.string.gab_clue_status_text), 
					User.getCurrentUser().getTotalClueCount()));
		}
	}

	@Override
	public void onUserChanged() {
		updateClueCount();		
	}
	
	@Override
	public void onChange(String action, int gabID, int objectID) {
		if(action == ClueObserver.CLUE_UPDATED) {
			Clue c = gab.getClueByID(objectID);		
			updateClue(c);
		}
	}

	private void updateClue(Clue c) {
		if(!c.isNew()) {
			ClueGridItem clueTile = new ClueGridItem(clueGrid, c.getNumber());
			clueTile.fillWithClue(c);
		}

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}
}
