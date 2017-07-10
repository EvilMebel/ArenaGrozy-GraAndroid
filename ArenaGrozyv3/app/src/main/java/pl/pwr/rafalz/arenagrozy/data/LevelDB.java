package pl.pwr.rafalz.arenagrozy.data;

import pl.pwr.rafalz.arenagrozy.R;

/**
 * DB of backgrounds and background music
 * @author Zientara
 *
 */
public class LevelDB {
	public static final int LEVEL_TYPE_FORREST = 1;
	public static final int LEVEL_TYPE_HOME = 2;
	public static final int LEVEL_TYPE_RUINS = 3;
	public static final int LEVEL_TYPE_SWAMP = 4;
	public static final int LEVEL_TYPE_DUNGEON = 5;
	
	public static LevelFrame getLevelBackgroundAndMusic(int backgType) {
		switch (backgType) {
		default:
		case LEVEL_TYPE_FORREST:
			return new LevelFrame(R.raw.music_game2_caverns, R.drawable.background_1forest3);
			
		case LEVEL_TYPE_HOME:
			return new LevelFrame(R.raw.music_game1_forthehorde, R.drawable.background_1forest3);
			
		case LEVEL_TYPE_RUINS:
			return new LevelFrame(R.raw.music_game3_easternsands, R.drawable.background_1forest3);
			
		case LEVEL_TYPE_SWAMP:
			return new LevelFrame(R.raw.music_game2_caverns, R.drawable.background_1forest3);
			
		case LEVEL_TYPE_DUNGEON:
			return new LevelFrame(R.raw.music_game3_easternsands, R.drawable.background_1forest3);
			

		}
	}
}
