package com.youtell.backdoor;

import com.youtell.backdoor.models.Message;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class MessageBubble {
	private Context context;
	private ViewGroup parent;
	private TextView message;
	private View views;

	public MessageBubble(Context context, ViewGroup parent) {
		this.context = context;
		this.parent = parent;
	}
	
	public View getViews()
	{
		this.views = LayoutInflater.from(context).inflate(R.layout.gab_detail_message_row_layout, parent, false);
		this.message = (TextView) this.views.findViewById(R.id.gab_message_text);
		this.views.setTag(this);
		return this.views;
	}
	
	public void fillWithMessage(Message m)
	{
		this.message.setText(m.text);
		
		LayoutParams lp = (LayoutParams) this.message.getLayoutParams();
		//check if it is a status message then remove background, and change text color.
		/*if(message.isStatusMessage())
		{
			holder.message.setBackgroundDrawable(null);
			lp.gravity = Gravity.LEFT;
			holder.message.setTextColor(R.color.textFieldColor);
		}
		else
		{*/		
			//Check whether message is mine to show green background and align to right
			if(m.isMine()) 
			{
				//holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
				lp.gravity = Gravity.RIGHT;
			}
			//If not mine then it is from sender to show orange background and align to left
			else
			{
				//holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
				lp.gravity = Gravity.LEFT;
			}
			this.message.setLayoutParams(lp);
			//holder.message.setTextColor(R.color.textColor);	
		//}
	}
}
