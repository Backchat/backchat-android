package com.youtell.backchat.iap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.youtell.backchat.Application;
import com.youtell.backchat.api.PostPurchasedClueRequest;
import com.youtell.backchat.models.User;
import com.youtell.backchat.services.APIService;
import com.youtell.backchat.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;

public class BuyClueIAP implements IAP.Observer {
	private IAP iap;
	private Activity activity;
	private User user;
	private Observer observer;
	
	public interface Observer {
		public void onReadyIAP();
		public void onBeginLoadIAP();
		public void onEndLoadIAP();
	}

	public <T extends Activity & Observer> BuyClueIAP(T activity) {
		this.activity = activity;
		this.observer = activity;
		this.iap = new IAP(this);
	}
	
	public void disconnect()
	{
		iap.disconnect();
	}

	private static final String[] iaps = {"clue_3", "clue_9", "clue_27"};

	public void present(User user) {
		this.user = user;
		this.observer.onBeginLoadIAP();
		iap.getItems(new ArrayList<String>(Arrays.asList(iaps)));
	}
	
	@Override
	public void onFailedUpdateItemList() {
		this.observer.onEndLoadIAP();
	}
	
	@Override
	public void onUpdateItemList(final List<Item> original_items) {
		this.observer.onEndLoadIAP();
		if(this.user == null)
			return;

		/* ugly workaround because we can't sort by price... */
		final List<Item> items = new ArrayList<Item>();
		
		for(String iap: iaps) {
			for(Item item: original_items) {
				if(item.getSKU().compareTo(iap) == 0) {
					items.add(item);
					break;
				}
			}
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		String[] itemsAsStringArray = new String[items.size()];
		int i=0;
		
		for(Item item : items) {
			itemsAsStringArray[i] = String.format("%s (%s)", item.description, item.price); //TODO stringify
			i++;
		}

		builder.setTitle("Buy")
		.setItems(itemsAsStringArray, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Item obj = items.get(which);
				iap.buy(obj, user);
			}
		})
		.setNegativeButton(R.string.cancel_button, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancel				
			}
			
		});
		
		builder.show();
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if(iap == null)
			return false;
		
		return iap.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onPurchaseSuccess(PurchasedItem item) {
		//TODO throw up a progress bar
		iap.consume(item);
	}

	@Override
	public void onConsumeSuccess(PurchasedItem item) {
		Log.e("IAP", "Should get some " + item.getSKU()); //TODO..change?
		//TODO better system here
		User.getCurrentUser().updateTotalClues(User.UNKNOWN_CLUE_COUNT);
		APIService.fire(new PostPurchasedClueRequest(item));
	}

	@Override
	public void onConnectedToIAP() {
		this.observer.onReadyIAP();
	}

	public void connect() {
		iap.connect(activity);
	}
}
