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
	private TextView statusLabel;

	public MessageBubble(Context context, ViewGroup parent) {
		this.context = context;
		this.parent = parent;
	}
	
	public View getViews()
	{
		this.views = LayoutInflater.from(context).inflate(R.layout.gab_detail_message_row_layout, parent, false);
		this.message = (TextView) this.views.findViewById(R.id.gab_message_text);
		this.statusLabel = (TextView) this.views.findViewById(R.id.gab_message_status_label);
		this.views.setTag(this);
		return this.views;
	}
	
	public void fillWithMessage(Message m, boolean isLast)
	{
		this.message.setText(m.text);
		
		int gravity;
		int messageBackground;		
		
		if(m.isMine()) 
		{
			gravity = Gravity.RIGHT;
			messageBackground = R.drawable.blue_bubble;
		}
		else
		{
			gravity = Gravity.LEFT;
			messageBackground = R.drawable.green_bubble;
		}

		LayoutParams lp = (LayoutParams) this.message.getLayoutParams();
		lp.gravity = gravity;
		this.message.setLayoutParams(lp);
		this.message.setBackgroundResource(messageBackground);

		//holder.message.setTextColor(R.color.textColor);	

		lp = (LayoutParams) this.statusLabel.getLayoutParams();
		lp.gravity = gravity;
		this.statusLabel.setLayoutParams(lp);
		
		if(m.isSent()) {
			if(isLast) {
				this.statusLabel.setText("Delivered");
			}		
			else {
				this.statusLabel.setVisibility(View.GONE);
			}
		}
		else {
			this.statusLabel.setText("Pending");
		}
	}
}
