package com.youtell.backdoor.iap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.youtell.backdoor.models.User;

public class IAP {
	public interface Observer {
		void onUpdateItemList(List<Item> items);
		void onPurchaseSuccess(PurchasedItem item);
		void onConsumeSuccess(PurchasedItem item);		
	}

	private IInAppBillingService billingService;
	private ServiceConnection billingConnection;
	private Observer observer;
	private Activity activity;

	public IAP(Observer obv) {
		observer = obv;
		
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

	public void connect(Activity act) {
		disconnect();
		
		activity = act;
		activity.bindService(new 
				Intent("com.android.vending.billing.InAppBillingService.BIND"),
				billingConnection, Context.BIND_AUTO_CREATE);
	}

	public void disconnect() {
		if(activity != null) {
			activity.unbindService(billingConnection);
			activity = null;
		}
	}

	private static final String[] iaps = {"clue_3", "clue_9", "clue_27"};

	public void getItems() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<String> skuList = new ArrayList<String>(Arrays.asList(iaps));
				Bundle querySkus = new Bundle();
				querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

				Bundle skuDetails = null;
				try {
					skuDetails = billingService.getSkuDetails(3, 
							activity.getPackageName(), "inapp", querySkus);
				} catch (RemoteException e) {
					Log.e("IAP","error", e);
					return;
				}

				int response = skuDetails.getInt("RESPONSE_CODE");
				if (response == 0) {
					ArrayList<String> responseList 
					= skuDetails.getStringArrayList("DETAILS_LIST");

					final ArrayList<Item> items = new ArrayList<Item>(responseList.size());

					try {
						for (String thisResponse : responseList) {
							JSONObject object;
							object = new JSONObject(thisResponse);
							items.add(new Item(object));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Log.e("IAP", "JSONERROR", e);
					}

					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							observer.onUpdateItemList(items);							
						}

					});
				}
			}
		});

		t.start();		
	}

	private static final int BUY_REQUEST_CODE = 1001;
	
	public void buy(Item obj, User user) {
		try {
			String sku = obj.getSKU();
			Bundle buyIntentBundle = billingService.getBuyIntent(3, activity.getPackageName(),
					sku, "inapp", 
					String.format("%d", user.getID()) /*dev payload*/);

			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

			if(pendingIntent == null) {
				/* we already bought it and we haven't consumed it; consume it so we can buy it again */

				String purchaseData = buyIntentBundle.getString("INAPP_PURCHASE_DATA");
				PurchasedItem purchased;
				purchased = new PurchasedItem(purchaseData);
				consume(purchased);
			}
			else {
				activity.startIntentSenderForResult(pendingIntent.getIntentSender(),
						IAP.BUY_REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
						Integer.valueOf(0));
			}
			
		} catch (Exception e) {
			// aTODO Auto-generated catch block
			Log.e("IAP", "buy", e);
		}		
	}

	public void consume(final PurchasedItem item) {
		final String token = item.getToken();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int response = billingService.consumePurchase(3, activity.getPackageName(), token);
					Log.e("IAP" ,String.format("response code %d", response));
					if(response == 0) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								observer.onConsumeSuccess(item);
							}
							
						});
						//success
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		t.start();
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) { 
		if (requestCode == BUY_REQUEST_CODE) {           
			int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			Log.e("IAP", String.format("%d %s", responseCode, dataSignature));

			if (resultCode == Activity.RESULT_OK) {
				Log.e("IAP", String.format("REQUEST SUCCESS: %s", purchaseData));
				
				try {
					PurchasedItem item = new PurchasedItem(purchaseData);
					observer.onPurchaseSuccess(item);					
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return true;
		}
		else
			return false;		   
	}
}
