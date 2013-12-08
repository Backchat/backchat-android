package com.youtell.backchat.tiles;

import com.youtell.backchat.models.Contact;
import com.youtell.backchat.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ContactTile extends Tile {
	private boolean showSelectedState;

	public ContactTile(Context context, ViewGroup parent) {
		super(context, parent);
		showSelectedState = true;
	}

	public ContactTile(Context context, View view) {
		super(context, view);
		showSelectedState = true;
	}

	public void setShowSelected(boolean show) {
		showSelectedState = show;
	}
	
	@Override
	public void fill(Object object) {
		setAlpha(1.0);
		
		Contact contact = (Contact)object;

		this.titleLabel.setText(contact.name);		
		this.subtitleLabel.setText(contact.number);
		this.attributeIcon.setVisibility(View.VISIBLE);
		this.timeLabel.setVisibility(View.GONE);

		if(contact.isSelected)
			this.attributeIcon.setBackgroundResource(R.drawable.select);
		else
			this.attributeIcon.setBackgroundResource(R.drawable.unselect);

		if(!showSelectedState) 
			this.attributeIcon.setVisibility(View.GONE);
		else
			this.attributeIcon.setVisibility(View.VISIBLE);
			
		loadAvatar(contact.photoURI);
	}
}