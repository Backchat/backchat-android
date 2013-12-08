package com.youtell.backchat;

import com.youtell.backchat.models.Message;
import com.youtell.backchat.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class MessageBubble {
	private Context context;
	private ViewGroup parent;
	private TextView message;
	private View views;
	private TextView headerTextLabel;
	private TextView statusLabel;
	private ImageView imageView;
	private LinearLayout backgroundBubble;
	private Object additionalInfo;
	private Callback listener;
	
	public interface Callback {
		void onImageClick(MessageBubble which, Object additionalInfo);
	}
	 
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
		this.imageView = (ImageView) this.views.findViewById(R.id.gab_message_image);
		this.backgroundBubble = (LinearLayout) this.views.findViewById(R.id.gab_message_background_bubble);
		
		this.imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null)
					listener.onImageClick(MessageBubble.this, MessageBubble.this.additionalInfo);
			}			
		});
		
		this.views.setTag(this);
		
		return this.views;
	}
	
	public void fillWithMessage(Message m, int fromRes, int toRes, int fromColor, int toColor, boolean isLast, boolean showHeader,
			Callback listener, Object additionalInfo)
	{
		if(m.getKind() == Message.KIND_TEXT) {
			this.message.setVisibility(View.VISIBLE);
			this.imageView.setVisibility(View.GONE);
			this.message.setText(m.getContent());
		}
		else {
			this.imageView.setVisibility(View.VISIBLE);
			this.message.setVisibility(View.GONE);			
			this.imageView.setImageBitmap(m.getThumbnailBitmap(context.getContentResolver()));
		}
		
		int gravity;
		int messageBackground;		
		int color;
		
		if(m.isMine()) 
		{
			gravity = Gravity.RIGHT;
			messageBackground = fromRes;
			color = fromColor;
		}
		else
		{
			gravity = Gravity.LEFT;
			messageBackground = toRes;
			color = toColor;
		}

		LayoutParams lp = (LayoutParams) this.backgroundBubble.getLayoutParams();
		lp.gravity = gravity;
		
		this.backgroundBubble.setLayoutParams(lp);
		this.backgroundBubble.setBackgroundResource(messageBackground);
		
		this.message.setTextColor(color);

		lp = (LayoutParams) this.statusLabel.getLayoutParams();
		lp.gravity = gravity;
		this.statusLabel.setLayoutParams(lp);
		
		if(m.isMine()) {
			this.statusLabel.setVisibility(View.VISIBLE);

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
		else 
			this.statusLabel.setVisibility(View.GONE);
		
		if(showHeader)
			this.headerTextLabel.setText(DateUtils.formatDateTime(context, m.getCreatedAt().getTime(), 
					DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
		else 
			this.headerTextLabel.setVisibility(View.GONE);
		
		this.listener = listener;
		this.additionalInfo = additionalInfo;
	}
}
