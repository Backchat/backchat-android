package com.youtell.backdoor;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.youtell.backdoor.models.Clue;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

//TODO  merge the cluetitle constructor
public class ClueGridItem {
	private View clueItem;
	private TextView label;
	private ImageView button;
	
	public ClueGridItem(LayoutInflater inflater, GridLayout clueGrid, int i, int width_count,
			int height_count, OnClickListener listener) {
		int x = i / height_count;
		int y = i % height_count;
		
		GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.columnSpec = GridLayout.spec(y);
		lp.rowSpec = GridLayout.spec(x);

		clueItem = inflater.inflate(R.layout.gab_clues_clue_button, null, false);

		button = (ImageView) clueItem.findViewById(R.id.gab_clues_clue_icon_button);
		button.setOnClickListener(listener);
		
		clueItem.setTag(Integer.valueOf(i));

		label = (TextView) clueItem.findViewById(R.id.gab_clues_clue_icon_text);
		
		clueGrid.addView(clueItem, lp);		
	}

	public ClueGridItem(GridLayout clueGrid, int i) {
		clueItem = clueGrid.findViewWithTag(Integer.valueOf(i));
		label = (TextView) clueItem.findViewById(R.id.gab_clues_clue_icon_text);
		button = (ImageView) clueItem.findViewById(R.id.gab_clues_clue_icon_button);
	}

	public void setLabel(String string) {
		label.setText(string);
	}

	public void fillWithClue(Clue c) {
		setLabel(c.getField());
		int pos = c.getValue().indexOf("|");
		String url = c.getValue().substring(0, pos);
		Log.e("cluetile", url);		
		button.setClickable(false);
		

		ImageLoader.getInstance().displayImage(url, button);
	}

}
