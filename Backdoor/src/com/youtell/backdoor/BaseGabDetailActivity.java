package com.youtell.backdoor;

import com.youtell.backdoor.dummy.DummyContent;
import com.youtell.backdoor.models.Gab;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

public class BaseGabDetailActivity extends BaseActivity {
	public static final String ARG_GAB_ID = "ARG_GAB_ID";
	
	protected Gab gab;
	protected String gabID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gab_detail);
        gabID = getIntent().getStringExtra(BaseGabDetailActivity.ARG_GAB_ID);
        gab = DummyContent.ITEM_MAP.get(gabID);
        setTitle(gab.getTitle());
	}
	
	protected void setupFragment(int fromRes, int toRes)
	{
		 Bundle arguments = new Bundle();
         arguments.putString(GabDetailFragment.ARG_GAB_ID,
                 gabID);
         arguments.putInt(GabDetailFragment.FROM_MESSAGE_RES, fromRes);
         arguments.putInt(GabDetailFragment.TO_MESSAGE_RES, toRes);
         GabDetailFragment fragment = new GabDetailFragment();
         fragment.setArguments(arguments);
         getFragmentManager().beginTransaction()
                 .add(R.id.gab_detail_container, fragment)
                 .commit();
	}
	
	protected void setupActionBar(boolean showButtons) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		//we have to custom layout the params because the custom view can't affect the parent in XML
		View cView = getLayoutInflater().inflate(R.layout.gab_detail_activity_bar_layout, null);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		actionBar.setCustomView(cView, lp);


		if(showButtons) {
			//hide the two buttons
			View clueButton = cView.findViewById(R.id.gab_clue_button);
			clueButton.setVisibility(View.GONE);
			View tagButton = cView.findViewById(R.id.gab_tag_button);
			tagButton.setVisibility(View.GONE);
		}
	}        
}
