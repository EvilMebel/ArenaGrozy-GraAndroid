package pl.pwr.rafalz.arenagrozy.view;

import java.util.List;

import pl.pwr.rafalz.arenagrozy.GameActivity;
import pl.pwr.rafalz.arenagrozy.LevelsActivity;
import pl.pwr.rafalz.arenagrozy.MenuMusic;
import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.adapters.LevelsFrame;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GridView extends ViewGroup {
	private int colCount = 4;
	private int rowCount = 3;
	private int padding;
	
	private List<LevelsFrame> levels = null;
	
	Drawable unfocused = null;

	public GridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GridView(Context context) {
		super(context);
		init();
	}

	private void init() {
		
		int white = getResources().getColor(android.R.color.white);
		int black = Color.argb(255, 220, 220, 220);//getResources().getColor(android.R.color.black);
		int size = colCount *  rowCount;
		padding = getResources().getDimensionPixelSize(R.dimen.padding_big);
		
		for(int i = 0; i<size; i++ ) {
			View v = new View(getContext());
			if(i%2 == 1)
				v.setBackgroundColor(white);
			else
				v.setBackgroundColor(black);
			
			addView(v);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		if(colCount!=0) {
			int frameW = MeasureSpec.getSize(widthMeasureSpec)/colCount-padding*3;
			int frameH = MeasureSpec.getSize(heightMeasureSpec)/rowCount-padding*3;
			
			frameW = MeasureSpec.makeMeasureSpec(frameW, MeasureSpec.EXACTLY);
			frameH = MeasureSpec.makeMeasureSpec(frameH, MeasureSpec.EXACTLY);
			
			int count = getChildCount();
			for(int i = 0; i<count; i++) {
				View v = getChildAt(i);
				v.measure(frameW, frameH);
				
			}
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(colCount!=0) {
			int frameW = (r-l)/colCount;
			int frameH = (b-t)/rowCount;
			int row = 0;
			int col = 0;
			
			int count = getChildCount();
			for(int i = 0; i<count; i++) {
				View v = getChildAt(i);
				v.layout(col*frameW+padding,row*frameH +padding, 
						(col+1)*frameW-padding*2,(row+1)*frameH-padding*2);
				
				//prepare position for next pixel
				col++; 
				if(col == colCount)
				{
					//next row
					row++;
					col = 0;
				}
			}
		}
	}

	public int getColCount() {
		return colCount;
	}

	public void setColCount(int colCount) {
		this.colCount = colCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public void setScreenSize(int col, int row) {
		removeAllViews();
		colCount = col;
		rowCount = row;
		//refresh
		init();
	}

	public List<LevelsFrame> getLevels() {
		return levels;
	}

	public void setLevels(List<LevelsFrame> levels) {
		this.levels = levels;
		
		refreshViews();
	}

	/**
	 * used in LevelActivity for preparing view
	 */
	private void refreshViews() {
		removeAllViews();
		for(int i = 0; i <levels.size(); i++) {
			final LevelsFrame lvl = levels.get(i);
			
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View levelView = inflater.inflate(R.layout.level_item, this,
					false);
			
			View level_go = levelView.findViewById(R.id.level_go);

			TextView level_number = (TextView) levelView.findViewById(R.id.level_number);
			ImageView level_locked = (ImageView) levelView.findViewById(R.id.level_locked);
			StarsView level_stars = (StarsView) levelView.findViewById(R.id.level_stars);

			if(!lvl.isLocked())
			{
				level_locked.setVisibility(View.GONE);
				level_stars.setStars(lvl.getStars());
			} else {
				level_stars.setVisibility(View.GONE);
			}
			
			level_number.setText(Integer.toString(lvl.getLevelNumber()));
			
		
			level_go.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(!lvl.isLocked()) {
						MenuMusic.playButtonSound(getContext(),R.raw.success1);
						
						Intent intent = new Intent(getContext(), GameActivity.class);
						intent.putExtra("LEVEL", lvl.getLevelNumber());
						getContext().startActivity(intent);
						LevelsActivity.expectedPause = false; //to turn off music when player choose level
					}
				}
			});
			
			addView(levelView);
		}
	}
}
