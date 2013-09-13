package com.youtell.backdoor.adapters;

import java.util.List;

import com.youtell.backdoor.MessageBubble;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GabDetailMessageAdapter extends BaseAdapter {

	private Context context;
	private List<Message> messages;
	private Gab item;
	
	public GabDetailMessageAdapter(Context context, Gab item) 
	{
		this.context = context;
		this.messages = item.getMessages();
		this.item = item;
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
		//TODO
		return 0;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
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

		boolean showHeader = false;
		if(position == 0)
			showHeader = true;
		else {
			Message prevMessage = (Message)this.getItem(position-1);
			long distance = message.getCreatedAt().getTime() - prevMessage.getCreatedAt().getTime();
			showHeader = distance > 2*60*1000; // 2 minutes
		}
		bubble.fillWithMessage(message, position == getCount() - 1, showHeader);
		
		return convertView;
	}
}
