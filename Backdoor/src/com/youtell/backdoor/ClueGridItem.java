package com.youtell.backdoor;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.youtell.backdoor.models.Clue;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

//TODO  merge the cluetitle constructor
public class ClueGridItem {
	private View clueItem;
	private TextView label;
	private ImageView button;
	private ProgressBar progress;
	
	public interface Callback {
		public void onClick(ClueGridItem which);
	}
	
	public ClueGridItem(LayoutInflater inflater, GridLayout clueGrid, int i, int width_count,
			int height_count, final Callback listener) {
		int x = i / height_count;
		int y = i % height_count;
		
		GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.columnSpec = GridLayout.spec(y);
		lp.rowSpec = GridLayout.spec(x);

		clueItem = inflater.inflate(R.layout.gab_clues_clue_button, null, false);

		button = (ImageView) clueItem.findViewById(R.id.gab_clues_clue_icon_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onClick(ClueGridItem.this);
			}
			
		});
		
		clueItem.setTag(Integer.valueOf(i));

		label = (TextView) clueItem.findViewById(R.id.gab_clues_clue_icon_text);

		progress = (ProgressBar) clueItem.findViewById(R.id.gab_clues_clue_icon_progress);
		
		progress.setVisibility(View.GONE);
		
		clueGrid.addView(clueItem, lp);		
	}

	public ClueGridItem(GridLayout clueGrid, int i) {
		clueItem = clueGrid.findViewWithTag(Integer.valueOf(i));
		label = (TextView) clueItem.findViewById(R.id.gab_clues_clue_icon_text);
		button = (ImageView) clueItem.findViewById(R.id.gab_clues_clue_icon_button);
		progress = (ProgressBar) clueItem.findViewById(R.id.gab_clues_clue_icon_progress);
	}

	public void setLabel(String string) {
		label.setText(string);
	}

	public void startProgress() {
		progress.setVisibility(View.VISIBLE);
		button.setImageResource(R.drawable.black_background);
	}
	
	public void fillWithClue(Clue c) {	
		setLabel(c.getDisplayText(clueItem.getContext()));
		int rounding = (int)clueItem.getContext().getResources().getDimension(R.dimen.clue_tile_rounding);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)				
				.displayer(new RoundedBitmapDisplayer(rounding))
				.showStubImage(R.drawable.black_background)
				.build();
		
		startProgress();
		
		ImageLoader.getInstance().displayImage(c.getURL(), button, options, new SimpleImageLoadingListener() {
	        @Override
	        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	        	progress.setVisibility(View.INVISIBLE);
	        }
		});
	}

	public int getNumber() {
		return ((Integer)clueItem.getTag()).intValue();
	}

}
