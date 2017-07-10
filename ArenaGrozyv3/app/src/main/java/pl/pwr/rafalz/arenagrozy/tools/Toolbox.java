package pl.pwr.rafalz.arenagrozy.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.game.GameObject;
import pl.pwr.rafalz.arenagrozy.view.GameView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Class for random values, sorting, holding bitmaps for game etc.
 * By changing value TEST_MODE you can set mode for testing.
 * @author Zientara
 *
 */
public class Toolbox {
	public static final boolean TEST_MODE = true;
	
	private static final String tag = "ToolBox";
	public static final Bitmap.Config bitmapConf = Bitmap.Config.ARGB_4444;;
	public static int screenWidth;
	public static int screenHeight;
	
	public static final int propSpriteW = 180;
	public static final int propSpriteH = 240;
	
	
	//size used for standard sprites
	public static int spriteWidth;
	public static int spriteHeight;
	
	public static List<BitmapFrames> bitmaps = new ArrayList<BitmapFrames>();
	// Use mGestureThreshold as a distance in pixels
	

	/**
	 * method called in first activity in all aplication to prepare all values in Toolbox
	 * @param c
	 */
	public static void initScreenInfo(Context c) {
		DisplayMetrics metrics = c.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		
		spriteHeight = screenHeight/4;//3;
		spriteWidth = propSpriteW * spriteHeight/ propSpriteH;
	}
	
	/**
	 * prepare array of bitmaps(for animation) from specified bitmapId
	 * @param bmp
	 * @param frameCount
	 * @return
	 */
	private static Bitmap[] getFramesFromBitmap(Bitmap bmp, int frameCount) {
		Bitmap[] frames = new Bitmap[frameCount];
		if(TEST_MODE)
			Log.d(tag, "Prepare frames from bmp " + bmp);
		int wid = bmp.getWidth() / frameCount;
		int hei = bmp.getHeight();
		
		int widthF = (int) (spriteWidth);
		int heightF = (int) (spriteHeight);
		
		//dont create bigger frames
		if(widthF>wid) {
			widthF = wid;
			heightF = hei;
		}
		
		Paint paint = getPaintStandard();

		for (int i = 0; i < frameCount; i++) {
			// empty bitmap
			Bitmap frame = Bitmap.createBitmap(widthF, heightF, bitmapConf);
			Canvas c = new Canvas(frame);
			int srcX = (i * wid);// to bedzie zmieniane w switch
			Rect src = new Rect(srcX, 0, srcX + wid, hei);
			Rect dst = new Rect(0, 0, widthF, heightF);
			c.drawBitmap(bmp, src, dst, paint);
			frames[i] = frame;
		}
		return frames;
	}
	
	/**
	 * searching wanted array of bitmaps in Toolbox's bitmap database
	 * @param bmpId
	 * @param mirrored
	 * @return
	 */
	private static int searchBmp(int bmpId, boolean mirrored) {
		int wantexIndex = -1;//-1 is not found
		for(int i = 0; i<bitmaps.size(); i++) {
			BitmapFrames b = bitmaps.get(i);
			if(b.bmpId==bmpId && b.mirrored == mirrored) {
				wantexIndex = i;
				break;//end of for
			}
		}
		return wantexIndex;
	}

	/**
	 * prepare array of bitmaps(for animation) from specified Bitmap
	 * @param bmp
	 * @param frameCount
	 * @return
	 */
	public static Bitmap[] getFramesFromBitmap(int bmpId, int framesCount) {
		Bitmap[] res = null;
		boolean mirrored = false;
		int wantexIndex = searchBmp(bmpId, mirrored);
		
		String drawable = getNameById(bmpId);
		
		Context load = GameView.game.get().getContext();
		
		if(wantexIndex== -1) { //not found - create new bitmap
			Bitmap bmp = BitmapFactory.decodeResource(load.getResources(), bmpId);
			res = getFramesFromBitmap(bmp, framesCount);
			bmp.recycle();
			BitmapFrames bf = new BitmapFrames(bmpId, framesCount, res, mirrored);
			bf.addOwner();
			bitmaps.add(bf);
			if(TEST_MODE)
				Log.d(tag, "TB Add new bmp " + drawable + " @"+res.hashCode() + " owners:"+bf.ownersCount );
			
		} else {
			res = bitmaps.get(wantexIndex).bmp;
			bitmaps.get(wantexIndex).addOwner();
			if(TEST_MODE)
				Log.d(tag, "TB Use existing bmp " + drawable + " @"+res.hashCode() + " owners:"+bitmaps.get(wantexIndex).ownersCount);
		}
		
		
		return res;
	}
	
	/**
	 * void for testing
	 * @param id
	 * @return
	 */
	private static String getNameById(int id) {
		switch (id) {
		case R.drawable.fox_stay:
			return "fox_stay";
		case R.drawable.fox_walk:
			return "fox_walk";
		case R.drawable.base_stay:
			return "base_stay";
		case R.drawable.base_walk:
			return "base_walk";

		default:
			return "def";
		}
	}
	
	/**
	 * if Toolbox have prepared array of animation you can get mirror version. In other case will return Exception
	 * @param bmpId
	 * @return
	 */
	public static Bitmap[] getMirroredFrames(int bmpId) {
		int index = searchBmp(bmpId, true);
		Bitmap[] res = null;
		
		if(index== -1) { //not found - create new bitmap
			Bitmap[] bmp = bitmaps.get(searchBmp(bmpId, false)).bmp;
			res = getMirroredFrames(bmp);

			BitmapFrames bf = new BitmapFrames(bmpId, res.length, res, true);
			bf.addOwner();
			bitmaps.add(bf);
			
		} else {
			res = bitmaps.get(index).bmp;
			bitmaps.get(index).addOwner();
		}
		
		return res;
	}

	/**
	 * if Toolbox have prepared array of animation you can get mirror version. In other case will return Exception
	 * @param bmpId
	 * @return
	 */
	private static Bitmap[] getMirroredFrames(Bitmap[] walkL) {
		Bitmap[] frames = new Bitmap[walkL.length];
		Bitmap bmp = walkL[0];
		int wid = bmp.getWidth();
		int hei = bmp.getHeight();
		//Paint paint = new Paint();
		
		Matrix matrix = new Matrix(); 
		matrix.preScale(-1.0f, 1.0f); 
		
		for(int i = 0; i < walkL.length;i++) {
			Bitmap mirroredBitmap = Bitmap.createBitmap(walkL[i], 0, 0, wid, hei, matrix, false);
			frames[i] = mirroredBitmap;
		}
		
		return frames;
	}

	
	/*
	 * 				RANDOM VALUES
	 */
	
	/**
	 * random float
	 * @param minX
	 * @param maxX
	 * @return
	 */
	public static float getRandom(float minX, float maxX) {
		Random rand = new Random();
		return rand.nextFloat() * (maxX - minX) + minX;
	}
	
	/**
	 * random int
	 * @param minX
	 * @param maxX
	 * @return
	 */
	public static int getRandom(int minX, int maxX) {
		Random rand = new Random();
		return rand.nextInt(maxX - minX + 1) + minX;
	}
	
	/*
	 * 						SORTING
	 */
	
	/**
	 * sorting for small amount of objects. Used for sorting Layers n GameView
	 * @param input
	 */
	public static void bubblesort(List<GameObject> input) { 
	    for(int i = input.size()-1; i >= 0; i--) {
	        for(int j = 0; j < i; j++) {
	            if(input.get(j).getY() > input.get(j + 1).getY()) {
	                GameObject temp = input.get(j);
	                input.set(j, input.get(j + 1));
	                input.set(j + 1, temp);
	            }
	        }
	    }
	}
	
	/*
	 * 					MEMORY MANAGEMENT
	 */
	
	/**
	 * scale down bitmap and recycle used bmp
	 * @param bmp
	 * @param scale
	 */
	public static Bitmap onLowMemoryBitmap(Bitmap bmp, float scale, int sWid, int sHei) {		
		int widthF = (int) (sWid * scale);
		int heightF = (int) (sHei * scale);
		
		
		Paint paint = getPaintStandard();

		// empty bitmap
		Bitmap frame = Bitmap.createBitmap(widthF, heightF, bitmapConf);
		Canvas c = new Canvas(frame);
		Rect dst = new Rect(0, 0, widthF, heightF);
		c.drawBitmap(bmp, null, dst, paint);
		
		bmp.recycle();
		return frame;
	}
	
	/**
	 * standard Paint used in all functions
	 * @return
	 */
	private static Paint getPaintStandard() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		//paint.setDither(true);
		return paint;
	}
	
	/**
	 * scale all images to specified scale
	 * @param scale
	 */
	public static void onLowMemoryBmpFrames(float scale) {
		for(int i = 0; i< bitmaps.size(); i++) {
			BitmapFrames b = bitmaps.get(i);
			if(TEST_MODE)
				Log.d(tag, "LM scale =" + scale + " isLower " + b.scaleLowMemory);
			if(b.scaleLowMemory>scale) {
				if(TEST_MODE)
					Log.d(tag, "LM scale bitmap to " + scale + "  " + b.bmp);
				for(int j = 0; j<b.framesCount; j++) {
					b.bmp[j] = onLowMemoryBitmap(b.bmp[j], scale,b.sWidth, b.sHeight);
				}
				
			}
		}
	}
	
	
	/**
	 * not recommended! use void with bmpId and scale instead
	 * @param bitmaps
	 */
	protected static void freeMemory(Bitmap[] bitmaps) {
		
		Log.d(tag, "Free momory "+ bitmaps.toString());
		for(int i = 0; i< bitmaps.length; i++) {
			bitmaps[i].recycle();
			bitmaps[i] = null;
		}
		
		bitmaps = null;
		System.gc();
		
	}

	/**
	 * recycle single Bitmap. if you want to recycle array Bitmap use another void.
	 * @param bmp
	 */
	public static void freeMemory(Bitmap bmp) {
		bmp.recycle();
		bmp = null;
		System.gc();
	}
	
	
	/**
	 * let Toolbox know that you dont use this Bitmap anymore. Toolbox will
	 * reduce amount of owners of this bitmap. If amount of users is 0 - will
	 * recycle Bitmap.
	 * 
	 * @param bmpId
	 * @param mirrored
	 */
	public static void freeMemory(int bmpId, boolean mirrored) {
		int index = searchBmp(bmpId, mirrored);
		if(index!=-1) {
			BitmapFrames b = bitmaps.get(index);
			b.free();
			if(b.canBeDeleted()){
				b.freeMemory();
				bitmaps.remove(index);
			}
				
		} else
			Log.d(tag, "bmpId " + bmpId + " not found!");
	}

	/**
	 * recycle all bitmaps in game
	 */
	public static void freeMemoryAll() {
		for(int i = bitmaps.size()-1; i>=0; i--) {
			BitmapFrames b = bitmaps.get(i);
			freeMemory(b.bmp);
			b.bmp = null;
			bitmaps.remove(i);
		}
	}

	/**
	 * error while touching a object given in pixels. For onTouch void
	 * @return
	 */
	public static int gettouchCor() {
		return Toolbox.spriteHeight/3 ;
	}
	
	/**
	 * error while touching a screen given in pixels. For onTouch void
	 * @return
	 */
	public static int gettouchErrorMax() {
		return Toolbox.spriteHeight/2 ;
	}
}
