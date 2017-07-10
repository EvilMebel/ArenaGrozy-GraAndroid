package pl.pwr.rafalz.arenagrozy.data;

import java.util.ArrayList;
import java.util.List;

import pl.pwr.rafalz.arenagrozy.adapters.LevelsFrame;
import android.content.Context;
import android.content.SharedPreferences;

public class DataBase {
	private static SharedPreferences sharedPref;
	private static SharedPreferences.Editor editor;

	private static final String nazwaBazy = "ArenaGrozyDB";

	// base options
	private static final String opt_music = "OPT_MUSIC_";
	private static final String opt_sound = "OPT_SOUND_";

	public static final int HEROES_COUNT = 3;
	public static final int LEVELS_COUNT = 17;
	/*
	 * HEROES DATAevery character have 6 skills - 3 base, 3 special
	 */
	// keys for heroes data
	private static final String hero1 = "H1_";
	private static final String hero2 = "H2_";
	private static final String hero3 = "H3_";

	public static final int[] hero_spec_att_count = { 3, 3, 3 };

	// skills level
	private static final String life = "HP_";
	private static final String strenght = "STR_";
	private static final String defence = "DF_";

	// special attacks current level. numbers from 1 to inf
	private static final String skill = "SA_";

	// free EXP
	private static final String exp = "EXP_";

	/*
	 * LEVELS DATA xx_PROGRESS = 0, 1, 2 OR 3 DEPENDING ON SCORE BASE IS -1 =
	 * LEVEL LOCKED
	 */

	private static String level_progress = "_LPROG_";

	
	/**
	 * prepare objects of reading and writing data
	 * @param context
	 */
	public static void initSharedPreferences(Context context) {
		sharedPref = context.getSharedPreferences(nazwaBazy,
				Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	/*
	 * BASE OPTIONS
	 */
	
	/**
	 * get music switch status
	 * @return
	 */
	public static boolean getOptMusic() {
		String key = opt_music;
		return getBoolean(key, false);
	}

	/**
	 * set music status on or off
	 * @param state
	 */
	public static void setOptMusic(boolean state) {
		String key = opt_music;
		setBoolean(state, key);
	}

	/**
	 * get sounds switch status
	 * @return
	 */
	public static boolean getOptSound() {
		String key = opt_sound;
		return getBoolean(key, false);
	}

	/**
	 * set sounds status on or off
	 * @param state
	 */
	public static void setOptSound(boolean state) {
		String key = opt_sound;
		setBoolean(state, key);
	}

	/*
	 * HEROES LEVELS
	 */

	/**
	 * prepare key value for reading/writing data
	 * @param whichHero
	 * @return
	 */
	private static String getHeroKeyByInt(int whichHero) {
		switch (whichHero) {
		default:
		case 1:
			return hero1;
		case 2:
			return hero2;
		case 3:
			return hero3;
		}
	}

	/**
	 * get all skills and stats for wanted hero
	 * @param whichHero
	 * @return
	 */
	public static int[] getHeroLevels(int whichHero) {
		String keyH = getHeroKeyByInt(whichHero);
		int special_attacks = hero_spec_att_count[whichHero - 1];
		int[] levels = new int[3 + special_attacks];
		int if_empty = 0; // all levels are 0 at the beggining
		// base skills
		levels[0] = getInt(keyH + life, if_empty);
		levels[1] = getInt(keyH + strenght, if_empty);
		levels[2] = getInt(keyH + defence, if_empty);

		for (int i = 0; i < special_attacks; i++) {
			levels[i + 3] = getHeroSpecLvl(whichHero, i);
		}
		return levels;
	}

	public static void setHeroLifeLvl(int whichHero, int value) {
		String key = getHeroKeyByInt(whichHero) + life;
		setInt(key, value);
	}

	public static void setHeroStrLvl(int whichHero, int value) {
		String key = getHeroKeyByInt(whichHero) + strenght;
		setInt(key, value);
	}

	public static void setHeroDefLvl(int whichHero, int value) {
		String key = getHeroKeyByInt(whichHero) + defence;
		setInt(key, value);
	}

	/**
	 * Set level of special attack for specified hero. whichSpec from 1 to inf
	 * 
	 * @param whichHero
	 * @param whichSpec
	 * @param value
	 */
	public static void setHeroSpecLvl(int whichHero, int whichSpec, int value) {
		String key = getHeroKeyByInt(whichHero) + skill + whichSpec;
		setInt(key, value);
	}

	public static int getHeroSpecLvl(int whichHero, int whichSpec) {
		String key = getHeroKeyByInt(whichHero) + skill + whichSpec;
		return getInt(key, 0);
	}

	/**
	 * set level for skill or stat for wanted hero:
	 * 0-life, 
	 * 1-str, 
	 * 2-def, 
	 * 3-5-special attacks
	 * @param whichHero
	 * @param value
	 * @param position
	 */
	public static void setHeroLvL(int whichHero, int value, int position) {
		switch (position) {
		case 0:
			setHeroLifeLvl(whichHero, value);
			break;
		case 1:
			setHeroStrLvl(whichHero, value);
			break;
		case 2:
			setHeroDefLvl(whichHero, value);
			break;
		default:
			setHeroSpecLvl(whichHero, position - 3, value);
			break;
		}

	}

	/*
	 * HEROES EXP
	 */

	public static int getHeroExp(int whichHero) {
		String key = getHeroKeyByInt(whichHero) + exp;
		return getInt(key, 0);
	}

	public static void setHeroExp(int whichHero, int value) {
		String key = getHeroKeyByInt(whichHero) + exp;
		setInt(key, value);
	}

	public static void addHeroExp(int whichHero, int value) {
		String key = getHeroKeyByInt(whichHero) + exp;
		int e = getHeroExp(whichHero) + value;
		setInt(key, e);
	}

	/*
	 * LEVEL PROGRESS
	 */

	public static int getLevelStars(int whichLevel) {
		String key = whichLevel + level_progress;
		return getInt(key, -1);
	}

	public static void setLevelStars(int whichLevel, int value) {
		String key = whichLevel + level_progress;
		int currlvl = getLevelStars(whichLevel);
		if (currlvl < value)
			setInt(key, value);
	}

	public static void setLevelStarsDowngrade(int whichLevel, int value) {
		String key = whichLevel + level_progress;
		setInt(key, value);
	}

	public static List<LevelsFrame> getLevelsProgressAll() {
		List<LevelsFrame> data = new ArrayList<LevelsFrame>();

		int name;
		int stars;
		for (int i = 0; i < LEVELS_COUNT; i++) {
			name = i + 1;
			stars = DataBase.getLevelStars(i + 1);
			data.add(new LevelsFrame(stars, name));
		}
		return data;
	}

	public static List<LevelsFrame> getLevelsProgress(int from, int to) {
		List<LevelsFrame> data = new ArrayList<LevelsFrame>();

		int name;
		int stars;
		for (int i = from; i < to + 1; i++) {
			name = i;
			stars = DataBase.getLevelStars(i);
			data.add(new LevelsFrame(stars, name));
		}
		return data;
	}

	public static void unlockLevel(int whichLevel) {
		int star = DataBase.getLevelStars(whichLevel);
		if (star == -1)
			DataBase.setLevelStars(whichLevel, 0);
	}

	private static void log(String key, int value, char akcja) {
		// Log.d(tag, "key = "+key + " =>" + value + " akcja:"+akcja);
	}

	private static void log(String key, boolean value, char akcja) {
		// Log.d(tag, "key = "+key + " =>" + value + "\r\nakcja:"+akcja);
	}

	/*
	 * TOOLS
	 */
	private static void setBoolean(boolean state, String key) {
		log(key, state, 's');
		editor.putBoolean(key, state);
		editor.commit();
	}

	private static boolean getBoolean(String key, boolean if_empty) {
		boolean value = sharedPref.getBoolean(key, if_empty);
		log(key, value, 'g');
		return value;
	}

	private static void setInt(String key, int value) {
		log(key, value, 's');
		editor.putInt(key, value);
		editor.commit();
	}

	private static int getInt(String key, int if_empty) {
		int value = sharedPref.getInt(key, if_empty);
		log(key, value, 'g');
		return value;
	}

	public static void resetAll() {
		resetSkills();
		resetLevels();
	}

	public static void resetLevels() {
		for (int i = 0; i < LEVELS_COUNT; i++) {
			setLevelStarsDowngrade(i + 1, -1);// locked = -1
		}

	}

	public static void unlockLevels() {
		for (int i = 0; i < LEVELS_COUNT; i++) {
			setLevelStars(i + 1, 0);// locked = -1
		}
	}

	public static void resetSkills() {

		// reset heroes
		for (int i = 0; i < HEROES_COUNT; i++) {
			setHeroExp(i + 1, 0);
			for (int j = 0; j < hero_spec_att_count[i] + 3; j++)
				setHeroLvL(i + 1, 0, j);
		}
	}
}
