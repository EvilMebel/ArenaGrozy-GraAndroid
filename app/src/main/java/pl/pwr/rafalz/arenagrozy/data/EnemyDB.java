package pl.pwr.rafalz.arenagrozy.data;

import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.game.Enemy;
import pl.pwr.rafalz.arenagrozy.game.Sprite;
import pl.pwr.rafalz.arenagrozy.game.Task;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import android.graphics.Color;

/**
 * Database of enemies. Wanted enemy can be get by id spefied by id in this class
 * @author Zientara
 *
 */
public class EnemyDB {

	//starts from 80 becouse lower numbers are used by heroes
	//enemy id is also his task id
	//if enemy is a healer - need a new task id +1000
	public static final int HEAL_SKILL = 1000;
	
	public static final int ENEMY_SMALL = 80;
	public static final int ENEMY_WIZARD = 81;
	public static final int ENEMY_HEALER = 82;
	//ONLY ID FOR HEALING SKILL FOR HEALER
	public static final int ENEMY_HEALER_HEAL_SKILL = ENEMY_HEALER +HEAL_SKILL;
	public static final int ENEMY_TANK   = 83;
	public static final int ENEMY_BOMB   = 84;
	public static final int ENEMY_WEAK   = 85;//rly weak enemy with 1 hp
	public static final int ENEMY_NORMAL = 86;
	
	public static final float LIFE_STANDARD = HeroDB.LIFE_STANDARD * 0.4f;
	public static final float LIFE_LEVEL = HeroDB.LIFE_LEVEL * 0.4f;
	public static final float LIFE_LITTLE = 0.3f;
	public static final float LIFE_LOW = 0.7f;
	public static final float LIFE_MEDIUM = 1f;
	public static final float LIFE_HIGH = 1.3f;

	public static final float STRENGTH_STANDARD = HeroDB.STRENGTH_STANDARD * 0.6f;
	public static final float STRENGTH_LEVEL = HeroDB.STRENGTH_LEVEL * 0.6f;
	public static final float STRENGTH_LOW = 0.7f;
	public static final float STRENGTH_MEDIUM = 1f;
	public static final float STRENGTH_HIGH = 1.2f;

	public static final float DEFENSE_STANDARD = HeroDB.DEFENSE_STANDARD * 0.6f;
	public static final float DEFENSE_LEVEL = HeroDB.DEFENSE_LEVEL * 0.6f;
	public static final float DEFENSE_LOW = 0.5f;
	public static final float DEFENSE_MEDIUM = 1f;
	public static final float DEFENSE_HIGH = 2f;

	public static final float SPEED_LOW = 0.5f;
	public static final float SPEED_MEDIUM = 1f;
	public static final float SPEED_HIGH = 1.2f;

	private static float getLife(float lifeRank, float level) {
		return LIFE_STANDARD * lifeRank + level * LIFE_LEVEL * lifeRank;
	}

	private static float getStrength(float strRank, float level) {
		return STRENGTH_STANDARD * strRank + level * STRENGTH_LEVEL * strRank;
	}

	private static float getDefense(float defRank, float level) {
		return DEFENSE_STANDARD * defRank + level * DEFENSE_LEVEL * defRank;
	}

	/**
	 * get enemy by id. Id database is in class EnemyDB
	 * @param whichEnemy
	 * @param level
	 * @return
	 */
	public static Enemy getEnemy(int whichEnemy, float level) {
		float x;
		float y = Toolbox.getRandom(0.5f, 0.9f) * Toolbox.screenHeight;

		float leftOrR = Toolbox.getRandom(0, 1);

		//random positon for spawn - out of screen!
		if (leftOrR == 1) {
			x = Toolbox.screenWidth * Toolbox.getRandom(-0.3f, -0.1f);
		} else {
			x = Toolbox.screenWidth * Toolbox.getRandom(1.1f, 1.3f);
		}

		Enemy e = null;
		Task t = null;
		switch (whichEnemy) {
		case ENEMY_SMALL:
			e = new Enemy(x, y, R.drawable.base_walk, 8, R.drawable.base_stay,
					8, 0.5f);
			e.setExperience(3, level, 2);
			// stats
			e.setHpStart(getLife(LIFE_LITTLE, level));
			e.setStrStart(getStrength(STRENGTH_MEDIUM, level));
			e.setDefStart(getDefense(DEFENSE_LOW, level));
			e.setStandardSpeed(SPEED_HIGH);
			e.setAiType(Enemy.AI_DEF_ATTACK_WEAK);

			e.setPaintFilter(Color.parseColor("#FFCC66"));// orange
			// set def hit
			t = TaskDB.getTask(whichEnemy, e, level);
			e.setTask_hit(t);
			break;

		case ENEMY_NORMAL:
			e = new Enemy(x, y, R.drawable.base_walk, 8, R.drawable.base_stay,
					8, 1f);
			e.setExperience(5, level, 3);
			// stats
			e.setHpStart(getLife(LIFE_MEDIUM, level));
			e.setStrStart(getStrength(STRENGTH_MEDIUM, level));
			e.setDefStart(getDefense(DEFENSE_MEDIUM, level));
			e.setStandardSpeed(SPEED_MEDIUM);
			e.setAiType(Enemy.AI_DEF_ATTACK_STRONG);

			e.setPaintFilter(Color.parseColor("#FFCC66"));// orange
			// set def hit
			t = TaskDB.getTask(whichEnemy, e, level);
			e.setTask_hit(t);
			break;
		case ENEMY_WIZARD:
			e = new Enemy(x, y, R.drawable.base_walk, 8, R.drawable.base_stay,
					8, 1f);
			e.setExperience(7, level, 4);
			// stats
			e.setHpStart(getLife(LIFE_MEDIUM, level));
			e.setStrStart(getStrength(STRENGTH_HIGH, level));
			e.setDefStart(getDefense(DEFENSE_LOW, level));
			e.setStandardSpeed(SPEED_MEDIUM);
			e.setAiType(Enemy.AI_DEF_ATTACK_NEAR);

			e.setPaintFilter(Color.parseColor("#9933FF"));// violet

			// set def hit
			t = TaskDB.getTask(whichEnemy, e, level);
			e.setTask_hit(t);
			break;
		case ENEMY_HEALER:
			e = new Enemy(x, y, R.drawable.base_walk, 8, R.drawable.base_stay,
					8, 1f);
			e.setExperience(5, level, 3);
			// stats
			e.setHpStart(getLife(LIFE_LOW, level));
			e.setStrStart(getStrength(STRENGTH_LOW, level));
			e.setDefStart(getDefense(DEFENSE_LOW, level));
			e.setStandardSpeed(SPEED_LOW);
			e.setAiType(Enemy.AI_HEAL_TANK);
			
			e.setPaintFilter(Color.GREEN);

			e.setHealer(true);// player 3 is a healer!
			// set def hit
			t = TaskDB.getTask(whichEnemy, e, level);
			e.setTask_hit(t);
			// set def heal
			t = TaskDB.getTask(EnemyDB.ENEMY_HEALER_HEAL_SKILL, e, level);
			e.setTask_heal(t);
			break;

		case ENEMY_TANK:
			e = new Enemy(x, y, R.drawable.base_walk, 8, R.drawable.base_stay,
					8, 1.3f);
			e.setExperience(10, level, 5);
			// stats
			e.setHpStart(getLife(LIFE_HIGH, level));
			e.setStrStart(getStrength(STRENGTH_HIGH, level));
			e.setDefStart(getDefense(DEFENSE_MEDIUM, level));
			e.setStandardSpeed(SPEED_LOW);
			e.setAiType(Enemy.AI_DEF_AGGRESSOR);

			e.setPaintFilter(Color.RED);

			// set def hit
			t = TaskDB.getTask(whichEnemy, e, level);
			e.setTask_hit(t);
			break;

		case ENEMY_BOMB:
			e = new Enemy(x, y, R.drawable.base_walk, 8, R.drawable.base_stay,
					8, 0.7f);
			e.setExperience(4, level, 3);
			// stats
			e.setHpStart(getLife(LIFE_LOW, level));
			e.setStrStart(getStrength(STRENGTH_MEDIUM, level));
			e.setDefStart(getDefense(DEFENSE_LOW, level));
			e.setStandardSpeed(SPEED_LOW);
			e.setAiType(Enemy.AI_DEF_ATTACK_RANDOM);

			e.setPaintFilter(Color.BLACK);

			// set def hit
			t = TaskDB.getTask(whichEnemy, e, level);
			e.setTask_hit(t);
			break;

		default:
		case ENEMY_WEAK:
			e = new Enemy(x, y, R.drawable.base_walk, 8, R.drawable.base_stay,
					8, 1f);
			e.setExperience(1, level, 1);
			// stats
			e.setHpStart(1);// weak!!
			e.setStrStart(getStrength(STRENGTH_LOW, level));
			e.setDefStart(getDefense(DEFENSE_LOW, level));
			e.setStandardSpeed(SPEED_MEDIUM);

			e.setPaintFilter(Color.parseColor("#cc5490"));// orange
			// set def hit
			t = TaskDB.getTask(ENEMY_WEAK, e, level);
			e.setTask_hit(t);
			break;

		}

		e.setEnemyId(whichEnemy);

		if (e.getTask_hit().getRange_mode() != Sprite.RANGE_CLOSE) {
			e.changeDestination(
					Toolbox.screenWidth * Toolbox.getRandom(0.1f, 0.2f),
					e.getY(), 0, false);
			if (leftOrR == 1) {
				e.changeDestination(
						Toolbox.screenWidth * Toolbox.getRandom(0.05f, 0.2f),
						e.getY(), 0, false);
			} else {
				e.changeDestination(
						Toolbox.screenWidth * Toolbox.getRandom(0.7f, 0.95f),
						e.getY(), 0, false);
			}
		}

		return e;
	}

}
