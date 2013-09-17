package com.youtell.backdoor;

import com.youtell.backdoor.models.Message;

import android.content.Context;
import android.text.format.DateUtils;
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
	private TextView headerTextLabel;
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
		this.headerTextLabel = (TextView) this.views.findViewById(R.id.gab_message_header_text);
		 
		this.views.setTag(this);
		return this.views;
	}
	
	public void fillWithMessage(Message m, int fromRes, int toRes, boolean isLast, boolean showHeader)
	{
		if(m.getKind() == Message.KIND_TEXT)
			this.message.setText(m.getContent());
		
		int gravity;
		int messageBackground;		
		
		if(m.isMine()) 
		{
			gravity = Gravity.RIGHT;
			messageBackground = fromRes;
		}
		else
		{
			gravity = Gravity.LEFT;
			messageBackground = toRes;
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
		
		if(showHeader)
			this.headerTextLabel.setText(DateUtils.formatDateTime(context, m.getCreatedAt().getTime(), 
					DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
		else 
			this.headerTextLabel.setVisibility(View.GONE);
	}
}
