package pl.pwr.rafalz.arenagrozy.view;

import pl.pwr.rafalz.arenagrozy.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class LevelBar extends View{
	
	private int maxLevel = 10;
	private int currentLevel = 0;
	private int minHeight = getResources().getDimensionPixelSize(R.dimen.padding_gigant);
	
	private Paint actPaint, unactPaint;
	
	private int padding = getResources().getDimensionPixelSize(R.dimen.padding_medium);
	private Rect r;
	
	private int mHeight;
	private int mWidth;

	public LevelBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LevelBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LevelBar(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		int colorActive = getResources().getColor(R.color.star_active);
		int colorUnactive = getResources().getColor(R.color.star_unactive);
		
		actPaint = new Paint();
		actPaint.setColor(colorActive);
		unactPaint = new Paint();
		unactPaint.setColor(colorUnactive);
		r = new Rect();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mHeight = minHeight*3;
		mWidth = (mHeight/2 + padding)*maxLevel + padding;
		int MesHei = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.AT_MOST);
		int MesWid = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
		super.onMeasure(MesWid, MesHei);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		r.set(padding, padding, padding + mHeight/2, mHeight-padding);
		for(int i = 0; i<maxLevel; i++) {
			if(i+1<=currentLevel)
				canvas.drawRect(r, actPaint);
			else
				canvas.drawRect(r, unactPaint);
			
			r.offset(padding + mHeight/2, 0);
			
		}
	}
	
	public void addLevel() {
		if(currentLevel<maxLevel)
			currentLevel++;
		requestLayout();
	}
	
	public void setLevel(int level) {
		currentLevel = level;
		if(currentLevel>maxLevel)
			currentLevel = maxLevel;
		requestLayout();
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
}
