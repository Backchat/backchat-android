package com.youtell.backchat.iap;

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
	
	public void present(User user) {
		this.user = user;
		this.observer.onBeginLoadIAP();
		iap.getItems();
	}
	
	@Override
	public void onFailedUpdateItemList() {
		this.observer.onEndLoadIAP();
	}
	
	@Override
	public void onUpdateItemList(final List<Item> items) {
		this.observer.onEndLoadIAP();
		if(this.user == null)
			return;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		String[] itemsAsStringArray = new String[items.size()];
		int i=0;
		
		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(Item lhs, Item rhs) {
				String lhsPrice = lhs.price;
				String rhsPrice = rhs.price;
				
				lhsPrice = lhsPrice.replaceAll("[^\\.0123456789]","");
				rhsPrice = rhsPrice.replaceAll("[^\\.0123456789]","");
				
				double lhsVal = Double.parseDouble(lhsPrice);
				double rhsVal = Double.parseDouble(rhsPrice);
				
				return Double.compare(lhsVal, rhsVal);
			}		
		});
		
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
