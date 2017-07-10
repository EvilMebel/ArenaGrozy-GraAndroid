package pl.pwr.rafalz.arenagrozy.data;

import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.view.GameView;
import android.content.Context;

public class TextDB {
	
	public final static int tdb_1first =1;
	public final static int tdb_2bomb =2;
	public final static int tdb_3wizard =3;
	public final static int tdb_4tank =4;
	public final static int tdb_5healer =5;
	public final static int tdb_6violence =6;
	public final static int tdb_7 =7;
	public final static int tdb_8 =8;
	public final static int tdb_9 =9;
	public final static int tdb_10 =10;
	public final static int tdb_11 =11;
	public final static int tdb_12 =12;
	public final static int tdb_13 =13;
	public final static int tdb_14 =14;
	public final static int tdb_15 =15;
	public final static int tdb_16 =16;
	public final static int tdb_17 =17;
	public final static int tdb_18 =18;
	public final static int tdb_19 =19;
	public final static int tdb_20 =20;

	public final static int hdb_1 =1;
	public final static int hdb_2 =2;
	public final static int hdb_3 =3;
	public final static int hdb_4 =4;
	public final static int hdb_5 =5;
	public final static int hdb_6 =6;
	public final static int hdb_7 =7;
	public final static int hdb_8 =8;
	public final static int hdb_9 =9;
	public final static int hdb_10 =10;
	public final static int hdb_11 =11;
	public final static int hdb_12 =12;
	public final static int hdb_13 =13;
	public final static int hdb_14 =14;
	/*
	 * 			hints
	 */
	
	public static String getText(int textId) {
		Context c = GameView.game.get().getContext();
		int resId = -1;
		
		switch (textId) {
		case tdb_1first:
			resId = R.string.tdb_1first;
			break;
			
		case tdb_2bomb:
			resId = R.string.tdb_2bomb;
			break;
			
		case tdb_3wizard:
			resId = R.string.tdb_3wizard;
			break;
			
		case tdb_4tank:
			resId = R.string.tdb_4tank;
			break;
			
		case tdb_5healer:
			resId = R.string.tdb_5healer;
			break;
			
		case tdb_6violence:
			resId = R.string.tdb_6violence;
			break;
			
		case tdb_7:
			resId = R.string.tdb_7;
			break;
			
		case tdb_8:
			resId = R.string.tdb_8;
			break;

		case tdb_9:
			resId = R.string.tdb_9;
			break;

		case tdb_10:
			resId = R.string.tdb_10;
			break;

		case tdb_11:
			resId = R.string.tdb_11;
			break;

		case tdb_12:
			resId = R.string.tdb_12;
			break;

		case tdb_13:
			resId = R.string.tdb_13;
			break;

		case tdb_15:
			resId = R.string.tdb_15;
			break;

		case tdb_16:
			resId = R.string.tdb_16;
			break;

		case tdb_17:
			resId = R.string.tdb_17;
			break;

		case tdb_18:
			resId = R.string.tdb_18;
			break;

		case tdb_19:
			resId = R.string.tdb_19;
			break;

		case tdb_20:
			resId = R.string.tdb_20;
			break;

		default:
			resId = R.string.tdb_error; 
			break;
		}
		
		return c.getResources().getString(resId);
	}
	
	public static int getHintText(int textId) {
		int resId = -1;
		
		switch (textId) {
		case hdb_1:
			resId = R.string.hdb_1;
			break;
			
		case hdb_2:
			resId = R.string.hdb_2;
			break;
			
		case hdb_3:
			resId = R.string.hdb_3;
			break;
			
		case hdb_4:
			resId = R.string.hdb_4;
			break;
			
		case hdb_5:
			resId = R.string.hdb_5;
			break;
			
		case hdb_6:
			resId = R.string.hdb_6;
			break;
			
		case hdb_7:
			resId = R.string.hdb_7;
			break;
			
		case hdb_8:
			resId = R.string.hdb_8;
			break;
			
		case hdb_9:
			resId = R.string.hdb_9;
			break;
			
		case hdb_10:
			resId = R.string.hdb_10;
			break;
			
		case hdb_11:
			resId = R.string.hdb_11;
			break;
			
		case hdb_12:
			resId = R.string.hdb_12;
			break;
			
		case hdb_13:
			resId = R.string.hdb_13;
			break;
			
		case hdb_14:
			resId = R.string.hdb_14;
			break;

		default:
			resId = R.string.tdb_error; 
			break;
		}
		
		return resId;
	}

}
