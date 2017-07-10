package pl.pwr.rafalz.arenagrozy.view;


import pl.pwr.rafalz.arenagrozy.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class StarsView extends View {

	private int stars;
	private final static int maxStars = 3;
	
	private int activeColor;
	private int unactiveColor;
	
	private Path[] Star;
	private Paint paintActive;
	private Paint paintUnactive;
	private Paint outline;
	
	public StarsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public StarsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public StarsView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		stars = 0;
		
		Star = new Path[maxStars];
		for(int i = 0; i< maxStars; i++)
			Star[i] = new Path();
		
		paintActive = new Paint();
		paintUnactive = new Paint();
		outline = new Paint();
		
		activeColor = getResources().getColor(R.color.star_active);
		unactiveColor = getResources().getColor(R.color.star_unactive);
		
		paintActive.setAntiAlias(true);
		paintActive.setColor(activeColor);
		
		paintUnactive.setAntiAlias(true);
		paintUnactive.setColor(unactiveColor);
		
		outline.setAntiAlias(true);
		outline.setColor(Color.WHITE);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		prepareAllStas();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//if level is unlocked
		if(stars>-1) {
			
			//active stars
			for(int i = 0; i< stars; i++) {
				canvas.drawPath(Star[i], paintActive);
			}
			
			//unactive stars
			for(int i = stars; i< maxStars; i++)
				canvas.drawPath(Star[i], paintUnactive);
			
		}
	}
	
	private void prepareAllStas() {
		int wid = getWidth()/4;
		int hei = getHeight()/2;
		
		float smallR = Math.min(wid/2*0.7f, hei/2*0.7f);
		float bigR = Math.min(wid/2, hei/2);
		
		prepareShockStar(wid, hei, smallR, Star[0]);
		prepareShockStar(wid*2, hei, bigR, Star[1]);
		prepareShockStar(wid*3, hei, smallR, Star[2]);
		
		requestLayout();
	}

	
	private void prepareShockStar(int x, int y, float rad, Path star) {
		int NUMBER_OF_ARMS = 5;
		float outRadius = rad;
		float inRadius = outRadius / 2;
		float angle = (float) (Math.PI / NUMBER_OF_ARMS);
		Point center = new Point((int) x, (int) (y));
		PointF point = new PointF();
		float starRotation = -angle/2;

		star.reset();
		for (int i = 0; i < 2 * NUMBER_OF_ARMS; i++) {
			// get ones out circle and next time inner circle
			double r = (i & 1) == 0 ? outRadius : inRadius;
			point.set((float) (center.x + Math.cos(i * angle + starRotation) * r), (float) (center.y + Math.sin(i * angle +starRotation) * r));
			if (i == 0)
				star.moveTo(point.x, point.y);
			else
				star.lineTo(point.x, point.y);
		}
		star.close();
	}
	
	public void setStars(int stars) {
		this.stars = stars;
		requestLayout();
	}
	
	
}
