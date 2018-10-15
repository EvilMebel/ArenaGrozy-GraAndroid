 package pl.pwr.rafalz.arenagrozy.data;

import java.util.ArrayList;
import java.util.List;

import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.adapters.StatsSkillFrame;
import pl.pwr.rafalz.arenagrozy.game.sprites.Hero;
import pl.pwr.rafalz.arenagrozy.game.effects.Pointer;
import pl.pwr.rafalz.arenagrozy.game.Task;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

/**
 * Database of heroes sprites, stats, costs of skills
 * @author Zientara
 *
 */
public class HeroDB {
	public static final float LIFE_STANDARD = 300;
	public static final float LIFE_LEVEL = 60;
	
	public static final float LIFE_LOW = 0.7f;
	public static final float LIFE_MEDIUM = 1f;
	public static final float LIFE_HIGH = 1.4f;
	
	public static final float STRENGTH_STANDARD = 15;
	public static final float STRENGTH_LEVEL = 8;
	public static final float STRENGTH_LOW = 0.8f;
	public static final float STRENGTH_MEDIUM = 1f;
	public static final float STRENGTH_HIGH = 1.3f;
	
	public static final float DEFENSE_STANDARD = 20;
	public static final float DEFENSE_LEVEL = 7;
	
	public static final float DEFENSE_LOW = 0.5f;
	public static final float DEFENSE_MEDIUM = 1f;
	public static final float DEFENSE_HIGH = 1.3f;
	
	
	private static float getLife(float lifeRank, int level) {
		return LIFE_STANDARD * lifeRank + level*LIFE_LEVEL*lifeRank;
	}
	
	private static float getStrength(float strRank, int level) {
		return STRENGTH_STANDARD * strRank + level*STRENGTH_LEVEL*strRank;
	}
	
	private static float getDefense(float defRank, int level) {
		return DEFENSE_STANDARD * defRank + level*DEFENSE_LEVEL*defRank;
	}
	

	
	
	
	
	/**
	 * get exp cost for wanted skills
	 * @param nameID
	 * @return
	 */
	public static int[] getCosts(int nameID) {
		switch (nameID) {
		// HERO 1
		case R.string.h1_life:
			return new int[] { 10, 30, 90, 160, 270, 380};
		case R.string.h1_strenght:
			return new int[] { 10, 45, 120, 210, 325 };
		case R.string.h1_defense:
			return new int[] { 15, 35, 95, 180, 225, 300, 450 };
		case R.string.h1_skill1:
			return new int[] { 10, 65, 110, 235, 355 };
		case R.string.h1_skill2:
			return new int[] { 15, 45, 85, 125, 205 };
		case R.string.h1_skill3:
			return new int[] { 25, 90, 150, 270, 370 };

			// HERO 1
		case R.string.h2_life:
			return new int[] { 20, 65, 125, 230, 315, 405 };
		case R.string.h2_strenght:
			return new int[] { 10, 35, 85, 125, 185, 60};
		case R.string.h2_defense:
			return new int[] { 20, 45, 105, 155, 215 };
		case R.string.h2_skill1:
			return new int[] { 15, 45, 95, 150, 215 };
		case R.string.h2_skill2:
			return new int[] { 25, 65, 110, 185, 230, 285 };
		case R.string.h2_skill3:
			return new int[] { 45, 95, 130, 255, 360 };

			// HERO 1
		case R.string.h3_life:
			return new int[] { 10, 45, 100, 165, 225, 305 };
		case R.string.h3_strenght:
			return new int[] { 10, 35, 85, 115 };
		case R.string.h3_defense:
			return new int[] { 25, 75, 125, 190, 225, 315};
		case R.string.h3_skill1:
			return new int[] { 15, 35, 85, 120, 185 };
		case R.string.h3_skill2:
			return new int[] { 35, 95, 135, 190, 210, 275 };
		case R.string.h3_skill3:
			return new int[] { 45, 85, 115, 165 };
		}
		// bedzie error!
		Log.e("HeroDB", "Cost not found!");
		return null;
	}
	
	public static List<StatsSkillFrame> getHero1Levels(Context c) {
		List<StatsSkillFrame> s = new ArrayList<StatsSkillFrame>();
		int[] cLevels = DataBase.getHeroLevels(1);
		s.add(new StatsSkillFrame(R.string.h1_life, R.string.help_life, cLevels[0]));
		s.add(new StatsSkillFrame(R.string.h1_strenght, R.string.help_strenght, cLevels[1]));
		s.add(new StatsSkillFrame(R.string.h1_defense, R.string.help_defense, cLevels[2]));
		
		//special attacks
		s.add(new StatsSkillFrame(R.string.h1_skill1, R.string.help_h1_skill1, cLevels[3]));
		s.add(new StatsSkillFrame(R.string.h1_skill2, R.string.help_h1_skill2, cLevels[4]));
		s.add(new StatsSkillFrame(R.string.h1_skill3, R.string.help_h1_skill3, cLevels[5]));
		
		return s;
	}
	
	public static List<StatsSkillFrame> getHero2Levels(Context c) {
		List<StatsSkillFrame> s = new ArrayList<StatsSkillFrame>();
		int[] cLevels = DataBase.getHeroLevels(2);
		s.add(new StatsSkillFrame(R.string.h2_life, R.string.help_life, cLevels[0]));
		s.add(new StatsSkillFrame(R.string.h2_strenght, R.string.help_strenght, cLevels[1]));
		s.add(new StatsSkillFrame(R.string.h2_defense, R.string.help_defense, cLevels[2]));
		
		//special attacks
		s.add(new StatsSkillFrame(R.string.h2_skill1, R.string.help_h2_skill1, cLevels[3]));
		s.add(new StatsSkillFrame(R.string.h2_skill2, R.string.help_h2_skill2, cLevels[4]));
		s.add(new StatsSkillFrame(R.string.h2_skill3, R.string.help_h2_skill3, cLevels[5]));
		
		return s;
	}
	
	public static List<StatsSkillFrame> getHero3Levels(Context c) {
		List<StatsSkillFrame> s = new ArrayList<StatsSkillFrame>();
		int[] cLevels = DataBase.getHeroLevels(3);
		s.add(new StatsSkillFrame(R.string.h3_life, R.string.help_life, cLevels[0]));
		s.add(new StatsSkillFrame(R.string.h3_strenght, R.string.help_strenght, cLevels[1]));
		s.add(new StatsSkillFrame(R.string.h3_defense, R.string.help_defense, cLevels[2]));
		
		//special attacks
		s.add(new StatsSkillFrame(R.string.h3_skill1, R.string.help_h3_skill1, cLevels[3]));
		s.add(new StatsSkillFrame(R.string.h3_skill2, R.string.help_h3_skill2, cLevels[4]));
		s.add(new StatsSkillFrame(R.string.h3_skill3, R.string.help_h3_skill3, cLevels[5]));
		
		return s;
	}
	
	/**
	 * prepare sprite for game. Will load levels from DataBase
	 * @param whichHero
	 * @return
	 */
	public static Hero getHero(int whichHero) {
		int sW = Toolbox.screenWidth/4;
		int sH = Toolbox.screenHeight/8;
		
		Hero h = null;
		Pointer p = null;
		Task t = null;
		int[] cLevels = DataBase.getHeroLevels(whichHero);
		int color;
		switch(whichHero) {
		default:
		case 1:
			h = new Hero(sW, 4*sH, R.drawable.fox_walk, 8, R.drawable.fox_stay, 8, 1f, 1);
			p = new Pointer(0, 0, h.getWidth()/3);
			p.setColor(Color.RED);
			h.setPointer(p);

			//stats
			h.setHpStart(getLife(LIFE_HIGH, cLevels[0]));
			h.setStrStart(getStrength(STRENGTH_LOW, cLevels[1]));
			h.setDefStart(getDefense(DEFENSE_HIGH, cLevels[2]));
			//set def hit
			t = TaskDB.getTask(TaskDB.STANDARD_H1, h, 0);
			h.setTask_hit(t);
			break;
		case 2:
			h = new Hero(sW*2, 6*sH, R.drawable.fox_walk, 8, R.drawable.fox_stay, 8, 1f, 2);
			p = new Pointer(0, 0, h.getWidth()/3);
			color = Color.parseColor("#0044dd");//blue
			p.setColor(color);
			h.setPointer(p);

			h.setPaintFilter(color);
			//stats
			h.setHpStart(getLife(LIFE_LOW, cLevels[0]));
			h.setStrStart(getStrength(STRENGTH_HIGH, cLevels[1]));
			h.setDefStart(getDefense(DEFENSE_LOW, cLevels[2]));
			
			//set def hit
			t = TaskDB.getTask(TaskDB.STANDARD_H2, h, 0);
			h.setTask_hit(t);
			break;
		case 3:
			h = new Hero(sW*3, 4*sH, R.drawable.fox_walk, 8, R.drawable.fox_stay, 8, 1f, 3);
			p = new Pointer(0, 0, h.getWidth()/3);
			color = Color.parseColor("#33cc5c");//green
			p.setColor(color);
			h.setPointer(p);

			h.setPaintFilter(color);
			h.setHealer(true);//player 3 is a healer!
			
			//stats
			h.setHpStart(getLife(LIFE_MEDIUM, cLevels[0]));
			h.setStrStart(getStrength(STRENGTH_LOW, cLevels[1]));
			h.setDefStart(getDefense(DEFENSE_MEDIUM, cLevels[2]));
			
			//set def hit
			t = TaskDB.getTask(TaskDB.STANDARD_H3, h, 0);
			h.setTask_hit(t);
			//set def heal
			t = TaskDB.getTask(TaskDB.STANDARD_H3_HEAL, h, 0);
			h.setTask_heal(t);
			break;
		}
		
		return h;
	}

}
