package com.youtell.backdoor;

import java.util.ArrayList;
import java.util.List;

import com.youtell.backdoor.dummy.DummyContent.DummyItem;
import com.youtell.backdoor.models.Message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GabDetailMessageAdapter extends BaseAdapter {

	private Context context;
	private List<Message> messages;
	private DummyItem item;
	
	public GabDetailMessageAdapter(Context context, DummyItem item) 
	{
		this.context = context;
		this.messages = new ArrayList<Message>();
		this.item = item;
		this.messages.add(new Message(item.content));
	}
	
	@Override
	public int getCount() {
		return this.messages.size();
	}

	@Override
	public Object getItem(int position) {
		return this.messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message message = (Message) this.getItem(position);

		MessageBubble bubble; 
		if(convertView == null)
		{
			bubble = new MessageBubble(context, parent);
			convertView = bubble.getViews();
		}
		else
			bubble = (MessageBubble) convertView.getTag();

		bubble.fillWithMessage(message);
		
		return convertView;
	}
}
