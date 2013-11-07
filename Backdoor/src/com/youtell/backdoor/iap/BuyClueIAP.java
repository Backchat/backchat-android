package com.youtell.backdoor.iap;

import java.util.List;

import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostPurchasedClueRequest;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.services.APIService;

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

	public BuyClueIAP(Activity activity) {
		this.activity = activity;
		this.iap = new IAP(this);
	}
	
	public void connect() {
		this.iap.connect(this.activity);
	}
	
	public void disconnect()
	{
		iap.disconnect();
	}
	
	public void present(User user) {
		iap.connect(activity);
		this.user = user;
		iap.getItems();
	}
	
	@Override
	public void onUpdateItemList(final List<Item> items) {
		if(this.user == null)
			return;
					
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
		User.getCurrentUser().setTotalClueCount(User.UNKNOWN_CLUE_COUNT);
		APIService.fire(new PostPurchasedClueRequest(item));
	}
}
