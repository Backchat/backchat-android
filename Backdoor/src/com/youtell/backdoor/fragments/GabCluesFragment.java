package com.youtell.backdoor.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import com.android.vending.billing.IInAppBillingService;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.ForeignCollection;
import com.youtell.backdoor.ClueGridItem;
import com.youtell.backdoor.R;
import com.youtell.backdoor.iap.IAP;
import com.youtell.backdoor.iap.IAP.Observer;
import com.youtell.backdoor.iap.Item;
import com.youtell.backdoor.models.Clue;
import com.youtell.backdoor.models.DatabaseObject;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.ClueObserver;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.services.APIService;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.TextView;
import android.os.IBinder;

//TODO spinny while loading
public class GabCluesFragment extends CallbackFragment<GabCluesFragment.Callbacks> 
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
			ClueGridItem clueTile = new ClueGridItem(inflater, clueGrid, i, 3, 3, new OnClickListener() {		
				@Override
				public void onClick(View v) {
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
		clueLabel.setText(String.format(getActivity().getResources().getString(R.string.gab_clue_status_text), 
				User.getCurrentUser().getTotalClueCount()));
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
}
