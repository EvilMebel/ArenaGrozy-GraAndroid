package pl.pwr.rafalz.arenagrozy.tools;

import android.graphics.Bitmap;

/**
 * container which holds information about scaled bitmap, and animation frames. 
 * @author Evil
 *
 */
public class BitmapFrames {
	public int bmpId;
	public int framesCount;
	public int sWidth;
	public int sHeight;
	public Bitmap[] bmp;
	public int ownersCount;//how many sprites uses this animation?
	public float scaleLowMemory;//1-at start, describes scale after onLowMemory
	public boolean mirrored;
	
	public BitmapFrames() {
	}
	
	public BitmapFrames(int resId, int framesCount, Bitmap[] bmp, boolean mirrored) {
		super();
		this.bmpId = resId;
		this.framesCount = framesCount;
		this.bmp = bmp;
		this.mirrored = mirrored;
		scaleLowMemory = 1f;
		sWidth = bmp[0].getWidth();
		sHeight = bmp[0].getHeight();
	}

	public void addOwner() {
		ownersCount++;
	}
	
	public void free() {
		ownersCount--;
	}
	
	public boolean canBeDeleted() {
		return ownersCount<=0;
	}

	/**
	 * recycle all frames
	 */
	public void freeMemory() {
		Toolbox.freeMemory(bmp);
	}
}
