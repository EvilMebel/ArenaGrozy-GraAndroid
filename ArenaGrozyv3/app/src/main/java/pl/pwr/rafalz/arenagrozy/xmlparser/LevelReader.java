package pl.pwr.rafalz.arenagrozy.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;


public class LevelReader {
	private static String tag = LevelReader.class.getName();
	private static final String ns = null;
	
	private static final String MARK_SECTION = "SECTION";
	private static final String MARK_BACKGROUND = "BACKGROUND";
	
	private static final String MARK_HINT = "HINT";
	private static final String MARK_EMEMY = "ENEMY";
	private static final String MARK_TEXT = "TEXT";
	
	
	private static final String BACKGROUND_ID = "id";
	
	private static final String HINT_ID = "id";
	private static final String ENEMY_ID = "id";
	private static final String ENEMY_LEVEL = "level";
	private static final String TEXT_ID = "id";
	private static final String TEXT_COLOR = "color";
	
	private static XmlPullParserFactory pullParserFactory;
	
	public static XMLLevelInfo open(int level,Activity act) {
		
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			    InputStream in_s = act.getAssets().open(getLevelPath(level));
		        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in_s, null);

	            return parseXML(parser);

		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//if failed
		return null;
	}
	
	
	
	private static XMLLevelInfo parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		XMLLevelInfo info = new XMLLevelInfo();
		List<List<XMLData>> data  = null;
		List<XMLData> section = null;
        int eventType = parser.getEventType();
        XMLEnemy enemy = null;
        XMLText text = null;
        XMLHint hint = null;
        

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	data = new ArrayList<List<XMLData>>();
                	Log.d(tag, "LVL START DOC " + name);
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    //Log.d(tag, "LVL START " + name);
                    
                    if(name.equals(MARK_SECTION)) {
                    	section = new ArrayList<XMLData>();
                    	data.add(section);
                    } else if (name.equals(MARK_EMEMY)){
                        enemy = readEnemy(parser);
                        section.add(enemy);
                    } else if (name.equals(MARK_HINT)) {
                    	hint = readHint(parser);
                    	section.add(hint);
                    } else if (name.equals(MARK_TEXT)) {
                    	text = readText(parser);
                    	section.add(text);
                    } else if (name.equals(MARK_BACKGROUND)) {
                    	info.setBackgroundType(readIntValue(parser, BACKGROUND_ID, 0));
                    }
                    
                    break;
            }
            eventType = parser.next();
        }
        
        info.setData(data);
        
        return info;
	}
	
	
	
	private static XMLEnemy readEnemy(XmlPullParser parser) throws IOException, XmlPullParserException {
		int enemyId = 0;
		float level = 0;
		
	    parser.require(XmlPullParser.START_TAG, ns, MARK_EMEMY);
	    String tag = parser.getName(); 
	    if (tag.equals(MARK_EMEMY)) {
	    	//read id
	    	enemyId = readIntValue(parser, ENEMY_ID, enemyId);
	    	
	    	//read level
	    	level = readFloatValue(parser, ENEMY_LEVEL, level);
	    }
	    return new XMLEnemy(enemyId, level);
	}
	
	private static XMLHint readHint(XmlPullParser parser) throws IOException, XmlPullParserException {
		int hintId = 0;
		
	    parser.require(XmlPullParser.START_TAG, ns, MARK_HINT);
	    String tag = parser.getName(); 
	    if (tag.equals(MARK_HINT)) {
	    	//read id
	    	hintId = readIntValue(parser, HINT_ID, hintId);
	    }
	    return new XMLHint(hintId);
	}
	
	private static XMLText readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		int textId = 0;
		int color = Color.BLACK;
		
	    parser.require(XmlPullParser.START_TAG, ns, MARK_TEXT);
	    String tag = parser.getName();
	    if (tag.equals(MARK_TEXT)) {
	    	//read id
	    	textId = readIntValue(parser, TEXT_ID, textId);
	    	
	    	//read color
	    	color = readColorValue(parser, TEXT_COLOR, color);
	    }
	    return new XMLText(textId, color);
	}
	
	
	private static int readIntValue(XmlPullParser parser, String attr, int ifEmpty) throws IOException, XmlPullParserException {
		String res = parser.getAttributeValue(null, attr);
    	if(res!=null)
    		try {
    		return Integer.valueOf(res);
    		} catch (Exception e) {
    			Log.e(tag, "ERROR PARSING INT :" + res);
    			return ifEmpty;
    		}
    	else 
    		Log.e(tag, "Error reading " + attr);
    	
    	return ifEmpty;
	}
	
	private static int readColorValue(XmlPullParser parser, String attr, int ifEmpty) throws IOException, XmlPullParserException {
		String res = parser.getAttributeValue(null, attr);
    	if(res!=null)
    		try {
    			return Color.parseColor(res);
        		} catch (Exception e) {
        			Log.e(tag, "ERROR PARSING COLOR :" + res);
        			return ifEmpty;
        		}
    	else 
    		Log.e(tag, "Error reading " + attr);
    	
    	return ifEmpty;
	}
	
	private static float readFloatValue(XmlPullParser parser, String attr, float ifEmpty) throws IOException, XmlPullParserException {
		String res = parser.getAttributeValue(null, attr);
    	if(res!=null)
    		try {
    			return Float.valueOf(res);
        		} catch (Exception e) {
        			Log.e(tag, "ERROR READING FLOAT :" + res);
        			return ifEmpty;
        		}
    		
    	else 
    		Log.e(tag, "ERROR READING FLOAT " + attr);
    	
    	return ifEmpty;
	}
	
	public static void printLevelInfo(XMLLevelInfo info) {
		List<List<XMLData>> data = info.getData();
		Log.d(tag, "LEVEL background id " + info.getBackgroundType());
		Log.d(tag, "LEVEL size " + data.size());
		for(List<XMLData> section : data) {
			Log.d(tag, "LEVEL SECTION si:"+data.size());
			for(XMLData d : section) {
				Log.d(tag, "LEVEL " + d.toString());
			}
		}
	}
	
	public static String getLevelPath(int level) {
		return level + ".xml";
	}

}
