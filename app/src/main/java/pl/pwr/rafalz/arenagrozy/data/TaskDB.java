package pl.pwr.rafalz.arenagrozy.data;

import android.graphics.Color;
import android.os.PowerManager;
import android.util.Log;
import android.widget.HeterogeneousExpandableList;
import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.game.Sprite;
import pl.pwr.rafalz.arenagrozy.game.Task;

public class TaskDB {
	static final String tag = "TaskDB";

	public static final int STANDARD_H1 = 0;
	public static final int STANDARD_H2 = 1;
	public static final int STANDARD_H3 = 2;
	public static final int STANDARD_H3_HEAL = 3;

	public static final int SPECIAL_H1_S1 = 4;
	public static final int SPECIAL_H1_S2 = 5;
	public static final int SPECIAL_H1_S3 = 6;

	public static final int SPECIAL_H2_S1 = 7;
	public static final int SPECIAL_H2_S2 = 8;
	public static final int SPECIAL_H2_S3 = 9;

	public static final int SPECIAL_H3_S1 = 10;
	public static final int SPECIAL_H3_S2 = 11;
	public static final int SPECIAL_H3_S3 = 12;

	// HEROES
	// skills power - standard hit or heal
	public static final float HERO_POWERS_STANDARD = 15;
	public static final float HERO_POWERS_LEVEL = 4;
	public static final float HERO_POWERS_LOW = 0.7f;
	public static final float HERO_POWERS_MEDIUM = 1f;
	public static final float HERO_POWERS_HIGH = 1.5f;
	public static final float HERO_POWERS_MIGHT = 3f;

	// skills power - special skills
	public static final float HERO_POWERA_STANDARD = 35;
	public static final float HERO_POWERA_LEVEL = 15;
	public static final float HERO_POWERA_LOW = 0.65f;
	public static final float HERO_POWERA_MEDIUM = 1f;
	public static final float HERO_POWERA_HIGH = 1.3f;
	public static final float HERO_POWERA_MIGHT = 2.f;
	public static final float HERO_POWERA_MEGA_MIGHT = 2.5f;

	// ENEMY
	// skills power - standard hit or heal
	public static final float ENEMY_POWERS_STANDARD = HERO_POWERS_STANDARD * 0.8f;
	public static final float ENEMY_POWERS_LEVEL = HERO_POWERS_LEVEL * 0.8f;
	public static final float ENEMY_POWERS_LOW = 0.7f;
	public static final float ENEMY_POWERS_MEDIUM = 1f;
	public static final float ENEMY_POWERS_HIGH = 1.7f;
	public static final float ENEMY_POWERS_MIGHT = 3f;

	// skills power - special skills
	public static final float ENEMY_POWERA_STANDARD = HERO_POWERA_STANDARD * 0.8f;
	public static final float ENEMY_POWERA_LEVEL = HERO_POWERA_LEVEL * 0.8f;
	public static final float ENEMY_POWERA_LOW = 0.65f;
	public static final float ENEMY_POWERA_MEDIUM = 1f;
	public static final float ENEMY_POWERA_HIGH = 1.8f;

	private static float getPowerStandH(float powSRank, float level) {
		return HERO_POWERS_STANDARD * powSRank + level * HERO_POWERS_LEVEL
				* powSRank;
	}

	private static float getPowerSpecialH(float powSRank, float level) {
		return HERO_POWERA_STANDARD * powSRank + level * HERO_POWERA_LEVEL
				* powSRank;
	}

	private static float getPowerStandE(float powSRank, float level) {
		return ENEMY_POWERS_STANDARD * powSRank + level * ENEMY_POWERS_LEVEL
				* powSRank;
	}

	/**
	 * generate duration for task. dur -lvl*minus. Will never return <0
	 * 
	 * @param duration
	 * @param level
	 * @param minus
	 * @return
	 */
	private static float getDurOrLoad(float duration, float level, float minus) {
		float dur = duration - level * minus;
		if (dur > 0)
			return dur;
		return 0;
	}

	private static float getR(float base, float level, float plus) {
		return base + level * plus;
	}

	/**
	 * get Task for hero or enemy by taskId. Heroes tasks are specified in
	 * TaskDB. Enemies tasks are specified in EnemyDB
	 * 
	 * @param taskId
	 * @param parent
	 * @param level
	 * @return
	 */
	public static Task getTask(int taskId, Sprite parent, float level) {
		Task t = null;
		int color;

		Log.d(tag, "TB NEW TASK id " + taskId);
		switch (taskId) {
		/*
		 * HEROES TASKS
		 */
		case STANDARD_H1:
			t = new Task(taskId, Sprite.RANGE_CLOSE, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(0.7f);
			t.setPower(getPowerStandH(HERO_POWERS_MEDIUM, level));
			t.setSoundId(R.raw.punch4); // OK

			break;
		case STANDARD_H2:
			t = new Task(taskId, Sprite.RANGE_FAR, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(0.9f);
			t.setPower(getPowerStandH(HERO_POWERS_HIGH, level));
			t.setSoundId(R.raw.spell3);

			t.setMissleMode(true);
			color = Color.BLUE;
			t.setMissleColor(color);

			t.setWave_mode(true);
			t.setWaveOnTarget(true);
			t.setWaveColor(color);
			t.setWaveTime(t.getDuration());
			t.setWaveR(parent.getWidth() / 2);
			t.setWaveDR(0.1f);
			break;
		case STANDARD_H3:
			t = new Task(taskId, Sprite.RANGE_FAR, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(.9f);
			t.setSoundId(R.raw.spell1);
			t.setPower(getPowerStandH(HERO_POWERS_LOW, level));

			t.setMissleMode(true);
			color = Color.RED;
			t.setMissleColor(color);

			t.setWave_mode(true);
			t.setWaveOnTarget(true);
			t.setWaveColor(color);
			t.setWaveTime(t.getDuration());
			t.setWaveR(parent.getWidth() / 2);
			t.setWaveDR(0.1f);
			break;

		case STANDARD_H3_HEAL:
			t = new Task(taskId, Sprite.RANGE_FAR, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(1.2f);
			t.setSoundId(R.raw.heal1);
			t.setPower(getPowerStandH(HERO_POWERS_MIGHT, level));

			t.setMissleMode(true);
			color = Color.GREEN;
			t.setMissleColor(color);

			t.setWave_mode(true);
			t.setWaveOnTarget(true);
			t.setWaveColor(color);
			t.setWaveTime(t.getDuration());
			t.setWaveDA(100);
			t.setWaveR(parent.getWidth() / 2);
			t.setWaveDR(0.1f);
			t.setHealingSkill(true);

			break;

		/*
		 * SPECIAL SKILLS - remember to set loading time! - del is 4 sec ORDER:
		 * -duration -loading -power -buffs(stun)
		 * 
		 * -special effects
		 */

		case SPECIAL_H1_S1: // chanrge!
			t = new Task(taskId, Sprite.RANGE_CLOSE, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(getDurOrLoad(0.5f, level, 0.05f));
			t.setLoading(getDurOrLoad(15, level, 0.7f));
			t.setPower(getPowerSpecialH(HERO_POWERA_HIGH, level));
			t.setRunSpeed(parent.getStandardSpeed() * getR(3, level, 0.5f));
			t.setSoundId(R.raw.punchflappy);

			t.setStunTime(getR(1, level, 0.5f));
			break;

		case SPECIAL_H1_S2:
			t = new Task(taskId, Sprite.RANGE_LOCAL, Task.ANIM_ONE_DIRECTION,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(0.4f);// always
			t.setLoading(getDurOrLoad(10, level, 0.5f));
			t.setHitR(parent.getWidth() * getR(1, level, 0.3f));
			t.setPower(getPowerStandH(HERO_POWERA_LOW, level));
			t.setSoundId(R.raw.explosion3);

			t.setWave_mode(true);
			t.setWaveR(t.getHitR());
			t.setWaveDR(0.5f);
			break;

		case SPECIAL_H1_S3: // ROAR!!
			t = new Task(taskId, Sprite.RANGE_GLOBAL, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(getDurOrLoad(1.5f, level, 0.1f));
			t.setLoading(getDurOrLoad(20, level, 1));
			t.setPower(getPowerStandH(HERO_POWERA_MEDIUM, level));
			t.setStunTime(getR(4, level, 0.5f));
			t.setSoundId(R.raw.roar1);

			t.setFill_mode(true);
			t.setFillColor(Color.YELLOW);
			t.setFillAlpha(100);
			t.setFillDA(200);

			break;

		// HERO 2
		case SPECIAL_H2_S1: 
			t = new Task(taskId, Sprite.RANGE_FAR, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(0.3f);// quick attack!
			t.setLoading(getDurOrLoad(20, level, 1.5f));
			t.setPower(getPowerSpecialH(HERO_POWERA_HIGH, level));
			t.setSoundId(R.raw.newspell1);

			t.setMissleMode(true);
			color = Color.RED;
			t.setMissleColor(color);
			t.setMissleScale(3);

			t.setWave_mode(true);
			t.setWaveOnTarget(true);
			t.setWaveColor(color);
			t.setWaveTime(t.getDuration());
			t.setWaveR(parent.getWidth() / 2);
			t.setWaveDR(0.1f);
			break;

		case SPECIAL_H2_S2: // cyclone
			t = new Task(taskId, Sprite.RANGE_FAR, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(getDurOrLoad(1.5f, level, 0.15f));
			t.setLoading(getDurOrLoad(20, level, 1.5f));
			t.setPower(getPowerSpecialH(HERO_POWERA_HIGH, level));
			t.setHitR(parent.getWidth() / 4 * (getR(2, level, 0.7f)));
			t.setSoundId(R.raw.newspell1);

			t.setMissleMode(true);
			color = Color.BLACK;
			t.setMissleColor(color);
			t.setMissleScale(3);

			t.setWave_mode(true);
			t.setWaveOnTarget(true);
			t.setWaveColor(color);
			t.setWaveTime(t.getDuration());
			t.setWaveR(t.getHitR());
			t.setWaveDR(0.1f);
			break;

		case SPECIAL_H2_S3:
			t = new Task(taskId, Sprite.RANGE_GLOBAL, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(getDurOrLoad(2, level, 0.1f));
			t.setLoading(getDurOrLoad(26, level, 2));
			t.setPower(getPowerSpecialH(HERO_POWERA_HIGH, level));
			t.setSoundId(R.raw.boom1);

			t.setFill_mode(true);
			t.setFillColor(Color.RED);
			t.setFillAlpha(100);
			t.setFillDA(50);

			break;

		// HERO 3 - HEALER
		case SPECIAL_H3_S1: // fire wave
			t = new Task(taskId, Sprite.RANGE_LOCAL, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(getDurOrLoad(1.6f, level, 0.15f));
			t.setLoading(getDurOrLoad(14, level, 2));
			t.setPower(getPowerSpecialH(HERO_POWERS_MEDIUM, level));
			t.setHitR(parent.getWidth() / 2 * getR(4, level, 1));
			t.setHealingSkill(true);
			t.setSoundId(R.raw.fire1);

			t.setWave_mode(true);
			t.setWaveColor(Color.GREEN);
			t.setWaveR(t.getHitR());
			t.setWaveDR(0.5f);
			break;

		case SPECIAL_H3_S2: // shock!
			t = new Task(taskId, Sprite.RANGE_LOCAL, Task.ANIM_ONE_DIRECTION,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(getDurOrLoad(1.5f, level, 0.15f));
			t.setLoading(getDurOrLoad(24, level, 2));
			t.setPower(getPowerSpecialH(HERO_POWERS_MEDIUM, level));
			t.setHitR(parent.getWidth() * getR(1.5f, level, 0.5f));
			t.setStunTime(getR(4, level, 1));
			t.setSoundId(R.raw.lighting1);

			t.setWave_mode(true);
			t.setWaveColor(Color.BLUE);
			t.setWaveR(t.getHitR());
			t.setWaveDR(0.5f);
			break;

		case SPECIAL_H3_S3:
			t = new Task(taskId, Sprite.RANGE_GLOBAL, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.fox_walk, 8, parent);
			t.setDuration(getDurOrLoad(2, level, 0.2f));
			t.setLoading(getDurOrLoad(22, level, 2f));
			t.setPower(getPowerSpecialH(HERO_POWERS_HIGH, level));
			t.setHealingSkill(true);
			t.setSoundId(R.raw.magic1);

			t.setFill_mode(true);
			t.setFillColor(Color.GREEN);
			t.setFillAlpha(100);
			t.setFillDA(200);

			break;

		// END HEROES SKILLS

		/*
		 * MOBS SKILLS dont have to set loading ordering like in prev section
		 */

		case EnemyDB.ENEMY_SMALL:
			t = new Task(taskId, Sprite.RANGE_CLOSE, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.base_walk, 8, parent);
			t.setDuration(1f); // const
			t.setSoundId(R.raw.punch5);

			t.setPower(getPowerStandE(ENEMY_POWERS_LOW, level));
			break;

		case EnemyDB.ENEMY_NORMAL:
			t = new Task(taskId, Sprite.RANGE_CLOSE, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.base_walk, 8, parent);
			t.setDuration(1f);
			t.setSoundId(R.raw.punch6);

			t.setPower(getPowerStandE(ENEMY_POWERS_MEDIUM, level));
			break;

		case EnemyDB.ENEMY_WIZARD:
			t = new Task(taskId, Sprite.RANGE_FAR, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.base_walk, 8, parent);
			t.setDuration(1.4f);
			t.setSoundId(R.raw.whoosh1);

			t.setPower(getPowerStandE(ENEMY_POWERS_HIGH, level));

			t.setMissleMode(true);
			color = Color.CYAN;
			t.setMissleColor(color);

			t.setWave_mode(true);
			t.setWaveOnTarget(true);
			t.setWaveR(parent.getWidth());
			t.setWaveColor(color);
			t.setWaveDR(0.5f);
			break;

		case EnemyDB.ENEMY_HEALER:
			t = new Task(taskId, Sprite.RANGE_FAR, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.base_walk, 8, parent);
			t.setDuration(1.4f);
			t.setPower(getPowerStandE(ENEMY_POWERS_LOW, level));
			t.setSoundId(R.raw.blast2);

			t.setMissleMode(true);
			color = Color.BLACK;
			t.setMissleColor(color);

			t.setWave_mode(true);
			t.setWaveOnTarget(true);
			t.setWaveColor(color);
			t.setWaveTime(t.getDuration());
			t.setWaveDA(100);
			t.setWaveR(parent.getWidth() / 2);
			t.setWaveDR(0.1f);

			break;

		case EnemyDB.ENEMY_HEALER_HEAL_SKILL:
			t = new Task(taskId, Sprite.RANGE_FAR, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.base_walk, 8, parent);
			t.setDuration(1.7f);
			t.setPower(getPowerStandE(ENEMY_POWERS_MEDIUM, level));
			t.setSoundId(R.raw.heal3);

			t.setMissleMode(true);
			color = Color.GREEN;
			t.setMissleColor(color);

			t.setWave_mode(true);
			t.setWaveOnTarget(true);
			t.setWaveColor(color);
			t.setWaveTime(t.getDuration());
			t.setWaveDA(100);
			t.setWaveR(parent.getWidth() / 2);
			t.setWaveDR(0.1f);
			t.setHealingSkill(true);

			break;

		case EnemyDB.ENEMY_TANK:
			t = new Task(taskId, Sprite.RANGE_CLOSE, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.base_walk, 8, parent);
			t.setDuration(1.0f);
			t.setSoundId(R.raw.punch2);

			t.setPower(getPowerStandE(ENEMY_POWERS_HIGH, level));
			break;

		case EnemyDB.ENEMY_BOMB:
			t = new Task(taskId, Sprite.RANGE_CLOSE, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.base_walk, 8, parent);
			t.setDuration(3f);
			t.setHitR(parent.getWidth() * getR(2, level, 0.1f));
			t.setPower(getPowerStandE(ENEMY_POWERS_MIGHT, level));
			t.setSuicideMode(true);
			t.setSoundId(R.raw.bomb1);

			t.setStunTime(getR(1, level, 0.2f));

			t.setWave_mode(true);
			t.setWaveTime(t.getDuration());
			t.setWaveOnTarget(false);
			t.setWaveDA(100);
			t.setWaveR(t.getHitR());
			t.setWaveDR(0.1f);

			t.setFill_mode(true);
			t.setFillColor(Color.YELLOW);
			break;

		default:
		case EnemyDB.ENEMY_WEAK:
			Log.e(tag, "TaskId is not supported! nr:" + taskId);
			t = new Task(taskId, Sprite.RANGE_CLOSE, Task.ANIM_TWO_DIRECTIONS,
					R.drawable.base_walk, 8, parent);
			t.setDuration(0.7f);
			t.setSoundId(R.raw.scary1);

			t.setPower(getPowerStandE(ENEMY_POWERS_MIGHT, level));

			break;
		}

		return t;
	}

	/**
	 * get special attack icon for button. if wrong id will return pause bitmapId
	 * @param spell
	 * @return
	 */
	public static int getTaskIcon(int spell) {
		switch (spell) {
		case SPECIAL_H1_S1:
			return R.drawable.skill_h1_s1;

		case SPECIAL_H1_S2:
			return R.drawable.skill_h1_s2;

		case SPECIAL_H1_S3:
			return R.drawable.skill_h1_s3;

		case SPECIAL_H2_S1:
			return R.drawable.skill_h2_s1;

		case SPECIAL_H2_S2:
			return R.drawable.skill_h2_s2;

		case SPECIAL_H2_S3:
			return R.drawable.skill_h2_s3;

		case SPECIAL_H3_S1:
			return R.drawable.skill_h3_s1;

		case SPECIAL_H3_S2:
			return R.drawable.skill_h3_s2;

		case SPECIAL_H3_S3:
			return R.drawable.skill_h3_s3;

		default:
			return R.drawable.pause;
		}
	}

}
