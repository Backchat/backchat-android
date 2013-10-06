package com.youtell.backdoor.tiles;

import com.youtell.backdoor.R;
import com.youtell.backdoor.R.drawable;
import com.youtell.backdoor.models.Contact;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ContactTile extends Tile {

	public ContactTile(Context context, ViewGroup parent) {
		super(context, parent);
	}

	public ContactTile(Context context, View view) {
		super(context, view);
	}

	@Override
	public void fill(Object object) {
		Contact contact = (Contact)object;
		// TODO Auto-generated method stub

		//public void fillWithContact(String name, String number, String photoURI, boolean selected) {
		this.titleLabel.setText(contact.name);		
		this.subtitleLabel.setText(contact.number);
		this.attributeIcon.setVisibility(View.VISIBLE);
		this.timeLabel.setVisibility(View.GONE);

		if(contact.isSelected)
			this.attributeIcon.setBackgroundResource(R.drawable.select);
		else
			this.attributeIcon.setBackgroundResource(R.drawable.unselect);

		loadAvatar(contact.photoURI);

	}
}