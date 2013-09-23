package com.youtell.backdoor.fragments;

import java.util.ArrayList;

import com.android.vending.billing.IInAppBillingService;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.ForeignCollection;
import com.youtell.backdoor.ClueTile;
import com.youtell.backdoor.R;
import com.youtell.backdoor.models.Clue;
import com.youtell.backdoor.models.DatabaseObject;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.ClueObserver;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.services.APIService;

import android.app.Fragment;
import android.content.ComponentName;
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
	}

	public static final String ARG_GAB_ID = "ARG_GAB_ID";
	private Gab gab;
	private GridLayout clueGrid;
	private TextView clueLabel;
	private ClueObserver clueObserver;
	private IInAppBillingService billingService;
	private ServiceConnection billingConnection;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		gab = Gab.getByID(getArguments().getInt(ARG_GAB_ID, -1)); //TODO
		super.onCreate(savedInstanceState);
		clueObserver = new ClueObserver(this, gab);
		

		billingConnection = new ServiceConnection() {
		   @Override
		   public void onServiceDisconnected(ComponentName name) {
billingService = null;
		   }

		   @Override
		   public void onServiceConnected(ComponentName name, 
		      IBinder service) {
			   billingService = IInAppBillingService.Stub.asInterface(service);
		   }
		};
	}       

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_gab_clues, container, false);

		clueGrid = (GridLayout)view.findViewById(R.id.gab_clues_grid);

		for(int i=0;i<gab.getClueCount();i++) {
			final int number = i;
			ClueTile clueTile = new ClueTile(inflater, clueGrid, i, 3, 3, new OnClickListener() {		
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
				//TODO IAP
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

	private Object userObserver;

	@Override
	public void onResume() {
		super.onResume();
		userObserver = UserObserver.registerObserver(this);
		clueObserver.startListening();
		gab.updateClues(); //in case of change
	}

	@Override
	public void onStop() {
		super.onStop();
		clueObserver.stopListening();
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

	@Override
	public void onChange(String action, int gabID, int objectID) {
		if(action == ClueObserver.CLUE_UPDATED) {
			Clue c = gab.getClueByID(objectID);		
			updateClue(c);
		}
	}

	private void updateClue(Clue c) {
		if(!c.isNew()) {
			ClueTile clueTile = new ClueTile(clueGrid, c.getNumber());
			clueTile.fillWithClue(c);
		}

	}

}
