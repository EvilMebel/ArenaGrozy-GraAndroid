package pl.pwr.rafalz.arenagrozy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Modified TextView which uses a half of height for text size
 * @author Zientara
 *
 */
public class FullTextView extends TextView {

	public FullTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FullTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FullTextView(Context context) {
		super(context);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		//height in pixels
		this.setTextSize(TypedValue.COMPLEX_UNIT_PX, getMeasuredHeight()/2);
	}

}
