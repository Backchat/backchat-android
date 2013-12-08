package com.youtell.backchat.adapters;

import java.util.ArrayList;
import java.util.List;

import com.youtell.backchat.MessageBubble;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.Message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class GabDetailMessageAdapter extends ORMListAdapter<Message> implements MessageBubble.Callback {
	public interface Callbacks
	{
		void onImageClick(Message which);
	}
	
	private Gab gab;
	private int fromMessageRes;
	private int toMessageRes;
	private int fromColor;
	private int toColor;
	private Callbacks callback;
	
	public GabDetailMessageAdapter(Context context, Gab gab, Callbacks callback,
			int fromMessageRes, int toMessageRes, int fromColor, int toColor) 
	{
		super(context);
		this.gab = gab;
		this.fromMessageRes = fromMessageRes;
		this.toMessageRes = toMessageRes;
		this.fromColor = fromColor;
		this.toColor = toColor;
		this.callback = callback;
		
		updateData();
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
		
		bubble.fillWithMessage(message, fromMessageRes, toMessageRes, fromColor, toColor, position == getCount() - 1, showHeader, 
				this, message);
		
		return convertView;
	}

	@Override
	protected List<Message> getList() {
		return new ArrayList<Message>(gab.getMessages());
	}

	@Override
	public void onImageClick(MessageBubble which, Object additionalInfo) {
		if(callback != null)
			callback.onImageClick((Message)additionalInfo);
	}
}
