package pl.pwr.rafalz.arenagrozy.data;

import android.graphics.Color;

import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.game.Task;
import pl.pwr.rafalz.arenagrozy.game.sprites.Enemy;
import pl.pwr.rafalz.arenagrozy.game.sprites.Sprite;
import pl.pwr.rafalz.arenagrozy.game.sprites.Stats;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;

/**
 * Database of enemies. Wanted enemy can be get by id spefied by id in this class
 *
 * @author Zientara
 */
public class EnemyDB {

    //starts from 80 because lower numbers are used by heroes
    //enemy id is also his task id
    //if enemy is a healer - need a new task id +1000
    public static final int HEAL_SKILL = 1000;

    public static final int ENEMY_SMALL = 80;
    public static final int ENEMY_WIZARD = 81;
    public static final int ENEMY_HEALER = 82;
    //ONLY ID FOR HEALING SKILL FOR HEALER
    public static final int ENEMY_HEALER_HEAL_SKILL = ENEMY_HEALER + HEAL_SKILL;
    public static final int ENEMY_TANK = 83;
    public static final int ENEMY_BOMB = 84;
    public static final int ENEMY_WEAK = 85;//rly weak enemy with 1 hp
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
     *
     * @param whichEnemy
     * @param level
     * @return
     */
    public static Enemy getEnemy(int whichEnemy, float level) {
        Stats.Builder statsBuilder = new Stats.Builder();
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
                // stats
                statsBuilder.setHpStart(getLife(LIFE_LITTLE, level));
                statsBuilder.setStrStart(getStrength(STRENGTH_MEDIUM, level));
                statsBuilder.setDefStart(getDefense(DEFENSE_LOW, level));
                statsBuilder.setStandardSpeed(SPEED_HIGH);

                e = createEnemy(x, y, 0.5f, statsBuilder);

                e.setAiType(Enemy.AI_DEF_ATTACK_WEAK);


                e.setExperience(3, level, 2);


                e.setPaintFilter(Color.parseColor("#FFCC66"));// orange
                // set def hit
                t = TaskDB.getTask(whichEnemy, e, level);
                e.setTask_hit(t);
                break;

            case ENEMY_NORMAL:
                // stats
                statsBuilder.setHpStart(getLife(LIFE_MEDIUM, level))
                .setStrStart(getStrength(STRENGTH_MEDIUM, level))
                .setDefStart(getDefense(DEFENSE_MEDIUM, level))
                .setStandardSpeed(SPEED_MEDIUM);

                e = createEnemy(x, y, 1f, statsBuilder);
                e.setExperience(5, level, 3);
                e.setAiType(Enemy.AI_DEF_ATTACK_STRONG);

                e.setPaintFilter(Color.parseColor("#FFCC66"));// orange
                // set def hit
                t = TaskDB.getTask(whichEnemy, e, level);
                e.setTask_hit(t);
                break;
            case ENEMY_WIZARD:
                // stats
                statsBuilder.setHpStart(getLife(LIFE_MEDIUM, level))
                        .setStrStart(getStrength(STRENGTH_HIGH, level))
                        .setDefStart(getDefense(DEFENSE_LOW, level))
                        .setStandardSpeed(SPEED_MEDIUM);

                e = createEnemy(x, y, 1f, statsBuilder);
                e.setExperience(7, level, 4);
                e.setAiType(Enemy.AI_DEF_ATTACK_NEAR);

                e.setPaintFilter(Color.parseColor("#9933FF"));// violet

                // set def hit
                t = TaskDB.getTask(whichEnemy, e, level);
                e.setTask_hit(t);
                break;
            case ENEMY_HEALER:
                // stats
                statsBuilder.setHpStart(getLife(LIFE_LOW, level))
                        .setStrStart(getStrength(STRENGTH_LOW, level))
                        .setDefStart(getDefense(DEFENSE_LOW, level))
                        .setStandardSpeed(SPEED_LOW);

                e = createEnemy(x, y, 1f, statsBuilder);
                e.setExperience(5, level, 3);
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
                // stats
                statsBuilder.setHpStart(getLife(LIFE_HIGH, level))
                        .setStrStart(getStrength(STRENGTH_HIGH, level))
                        .setDefStart(getDefense(DEFENSE_MEDIUM, level))
                        .setStandardSpeed(SPEED_LOW);

                e = createEnemy(x, y, 1.3f, statsBuilder);
                e.setExperience(10, level, 5);
                e.setAiType(Enemy.AI_DEF_AGGRESSOR);

                e.setPaintFilter(Color.RED);

                // set def hit
                t = TaskDB.getTask(whichEnemy, e, level);
                e.setTask_hit(t);
                break;

            case ENEMY_BOMB:
                // stats
                statsBuilder.setHpStart(getLife(LIFE_LOW, level))
                        .setStrStart(getStrength(STRENGTH_MEDIUM, level))
                        .setDefStart(getDefense(DEFENSE_LOW, level))
                        .setStandardSpeed(SPEED_LOW);

                e = createEnemy(x, y, 0.7f, statsBuilder);
                e.setExperience(4, level, 3);
                e.setAiType(Enemy.AI_DEF_ATTACK_RANDOM);

                e.setPaintFilter(Color.BLACK);

                // set def hit
                t = TaskDB.getTask(whichEnemy, e, level);
                e.setTask_hit(t);
                break;

            default:
            case ENEMY_WEAK:
                // stats
                statsBuilder.setHpStart(1)// weak!!
                        .setStrStart(getStrength(STRENGTH_LOW, level))
                        .setDefStart(getDefense(DEFENSE_LOW, level))
                        .setStandardSpeed(SPEED_MEDIUM);

                e = createEnemy(x, y, 1f, statsBuilder);
                e.setExperience(1, level, 1);
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

    private static Enemy createEnemy(float x, float y, float scale, Stats.Builder statsBuilder) {
        Enemy e;
        e = new Enemy(x, y, R.drawable.base_walk, 8, R.drawable.base_stay,
                8, scale, statsBuilder.build());
        return e;
    }

}
