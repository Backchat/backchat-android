package com.youtell.backdoor.activities;

import java.util.List;

import com.youtell.backdoor.R;
import com.youtell.backdoor.api.PostPurchasedClueRequest;
import com.youtell.backdoor.fragments.GabCluesFragment;
import com.youtell.backdoor.fragments.GabDetailFragment;
import com.youtell.backdoor.iap.IAP;
import com.youtell.backdoor.iap.Item;
import com.youtell.backdoor.iap.PurchasedItem;
import com.youtell.backdoor.models.User;
import com.youtell.backdoor.observers.UserObserver;
import com.youtell.backdoor.services.APIService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

//TODO refactor all these userObservers
public class GabAnonymousDetailActivity extends BaseGabDetailActivity 
implements GabCluesFragment.Callbacks, IAP.Observer, UserObserver.Observer {
	private GabCluesFragment cluesFragment;
	private View cluesView;
	private IAP iap;
	private User user;
	private Object userObserver;

	public void tagGab(View v) {
    	final EditText tagName = new EditText(this);

    	tagName.setText(gab.getRelatedUserName());

    	new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
    	  .setTitle(R.string.tag_dialog_title)
    	  .setMessage(R.string.tag_dialog_body)
    	  .setView(tagName)
    	  .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	      gab.setRelatedUserName(tagName.getText().toString());
    	      gab.save();
    	      gab.updateTag(); //TODO?
    	    }
    	  })
    	  .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	    }
    	  })
    	  .show(); 	    
    }    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(SHOW_ANON_BUTTONS | SHOW_DELETE_BUTTON);
                
        if(savedInstanceState == null) {
        	setupFragment(R.drawable.blue_bubble, R.drawable.black_bubble_from, R.color.blue_bubble_text_color, R.color.black_bubble_from_text_color);
        }        
        
		Bundle arguments = new Bundle();
		arguments.putInt(GabCluesFragment.ARG_GAB_ID, gabID);
		cluesFragment = new GabCluesFragment();
		cluesFragment.setArguments(arguments);
		getFragmentManager().beginTransaction()
		.add(R.id.gab_clues_container, cluesFragment)
		.commit();
		
		cluesView = findViewById(R.id.gab_clues_container);
    	cluesView.setVisibility(View.GONE);

		//TODO move out IAP stuff?
		iap = new IAP(this);
		iap.connect();
    }
    
    public void onCluesClick(View v) {
    	//hide the keyboard first..
	    InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		
    	cluesView.setVisibility(View.VISIBLE);
    }
   
    
    @Override
    public void onResume()
    {
    	super.onResume();
		userObserver = UserObserver.registerObserver(this);
    }

	@Override
	public void onCancel() {
		cluesView.setVisibility(View.GONE);
	}

	@Override
	public void onUpdateItemList(final List<Item> items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		iap.disconnect();
	}
	
	@Override
	public void onBuy() {
		iap.getItems();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)    
	{
		if(!iap.onActivityResult(requestCode, resultCode, data))			
			super.onActivityResult(requestCode, resultCode, data);
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
		APIService.fire(new PostPurchasedClueRequest(item));
	}
    
	@Override
	public void onStop() {
		super.onStop();
		UserObserver.unregisterObserver(userObserver);
	}

	@Override
	public void onUserChanged() {	
	}

	@Override
	public void onUserSwapped(User old, User newUser) {
		user = newUser;
	}
}
