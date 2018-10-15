package pl.pwr.rafalz.arenagrozy.game.sprites;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import pl.pwr.rafalz.arenagrozy.data.HeroDB;
import pl.pwr.rafalz.arenagrozy.game.GameObject;
import pl.pwr.rafalz.arenagrozy.game.Task;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import pl.pwr.rafalz.arenagrozy.view.GameView;


public class Enemy extends Sprite {
    private final static String tag = Sprite.class.getName();

    //attack the weakest hero
    public static final int AI_DEF_ATTACK_WEAK = 0;
    //attack the nearest hero
    public static final int AI_DEF_ATTACK_NEAR = 1;
    // attack random hero
    public static final int AI_DEF_ATTACK_RANDOM = 2;
    //attack hero who is beating me the most - AngerFrame
    public static final int AI_DEF_AGGRESSOR = 3;
    //attack the strongest sprite
    public static final int AI_DEF_ATTACK_STRONG = 4;

    //heal the strongest mob
    public static final int AI_HEAL_TANK = 5;

    //levels of hp when healer will change behaviour
    public static final float HEAL_PROC_LOW = 0.5f;
    public static final float HEAL_PROC_MAX = 0.9f;

    //experience for defeating mob
    private int experience;
    private int aiType;

    //true if all heroes are dead
    private boolean evilWon;

    private List<HateFrame> blackList;

    private int enemyId;

    //will keep information about target durning stun
    private Sprite favTarget;

    public Enemy(float x, float y, int IDwalk, int walkFrames, int IDstay, int stayFrames, float scale) {
        super(IDwalk, walkFrames, IDstay, stayFrames, scale);
        this.x = x;
        this.y = y;

        // 	DEF VALUES
        experience = 5;
        aiType = AI_DEF_AGGRESSOR;
        paint.setColor(Color.WHITE);

        blackList = new ArrayList<Enemy.HateFrame>();
        useBrain();
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        if (DEBUG_MODE) {
            c.drawText(Integer.toString(enemyId), x, y, paint);
        }
    }


    /**
     * choose target depending on AI type
     */
    @Override
    protected void useBrain() {
        if (target == null && favTarget != null) {//back to my enemy - for RANDOM AI
            target = favTarget;
        }

        if (target == null && !evilWon) {

            List<GameObject> sprites = null;
            boolean lowHp = getHpProc() < HEAL_PROC_LOW;
            Log.d(tag, "attack! AI TYPE = " + aiType);
            switch (aiType) {

                case AI_DEF_AGGRESSOR://attack hero who is beating me the most - AngerFrame
                    if (blackList.isEmpty())
                        target = GameView.game.get().getNearestHero(x, y);
                    else
                        target = getHatedSprite();


                    break;

                case AI_DEF_ATTACK_NEAR://attack the nearest hero
                    target = GameView.game.get().getNearestHero(x, y);
                    break;

                default:
                case AI_DEF_ATTACK_RANDOM: // attack random hero
                    sprites = GameView.game.get().getHeroes();
                    target = getRandomSprite(sprites);//(Sprite) sprites.get(Toolbox.getRandom(0, size-1));
                    favTarget = target;
                    break;

                case AI_DEF_ATTACK_WEAK: //attack the weakest hero - min HP
                    sprites = GameView.game.get().getHeroes();
                    target = getWeakSprite(sprites);
                    break;

                case AI_DEF_ATTACK_STRONG: //attack the weakest hero - min HP
                    sprites = GameView.game.get().getHeroes();
                    target = getStrongSprite(sprites);
                    break;

				/*
				 *				HEALER AI 
				 */

                case AI_HEAL_TANK:

                    sprites = GameView.game.get().getEnemies();
                    target = getStrongSprite(sprites);
                    if (target == null || target == this || lowHp || (target != null && target.getHpProc() >= HEAL_PROC_MAX)) {
                        if (lowHp) {
                            target = this; // heal myself
                        } else {
                            // attack weak hero
                            sprites = GameView.game.get().getHeroes();
                            target = getRandomSprite(sprites);
                        }
                    }
                    break;
            }
        }

        if (target == null) {
            evilWon = true;
        } else {
            setTarget(target);
        }
    }

    private Sprite getRandomSprite(List<GameObject> sprites) {
        List<GameObject> list = new ArrayList<GameObject>();
        for (GameObject g : sprites) {
            if (!g.isDead())
                list.add(g);
        }

        if (list.size() > 0) {
            return (Sprite) list.get(Toolbox.getRandom(0, list.size() - 1));
        } else
            return null;
    }

    /**
     * get sprite with min hp
     *
     * @param list
     * @return
     */
    private Sprite getWeakSprite(List<GameObject> list) {
        if (list.size() > 0) {
            float min = (HeroDB.LIFE_STANDARD * 1000);
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                Sprite s = (Sprite) list.get(i);
                if (!s.isDead() && s.hp < min) {
                    min = s.hp;
                    index = i;
                }
            }
            if (index != -1)
                return (Sprite) list.get(index);
        }

        return null; // empty list or all enenmies are dead
    }

    /**
     * get sprite with the highest hp
     *
     * @param sprites
     * @return
     */
    private Sprite getStrongSprite(List<GameObject> sprites) {
        if (sprites.size() > 0) {
            float max = 0;
            int index = -1;
            for (int i = 0; i < sprites.size(); i++) {
                Sprite s = (Sprite) sprites.get(i);
                if (!s.isDead() && s.hp > max) {
                    max = s.hp;
                    index = i;
                }
            }
            if (index != -1)
                return (Sprite) sprites.get(index);
        }

        return null; // empty list
    }

    @Override
    public float hit(Task hitter, float stunT) {
        float dmg = super.hit(hitter, stunT);
        hateSprite(hitter.getParent(), dmg);

        if (hp > 0 && status != STATUS_STUN) {
            setTarget(null);
            useBrain();//refresh hated enemy
        }

        return dmg;
    }

    @Override
    public float heal(Task healer) {
        float heal = super.heal(healer);
        Sprite h = healer.getParent();

        if (h.target != null && getHpProc() >= HEAL_PROC_MAX) {
            h.target = null;
            if (h instanceof Enemy) {
                Enemy mHealer = (Enemy) h;
                mHealer.setTarget(null);
                mHealer.useBrain();
            }
        }

        return heal;
    }

    @Override
    public boolean isHealer() {
        return (aiType == AI_HEAL_TANK);
    }

    @Override
    public void spriteIsDead(Sprite sprite) {
        super.spriteIsDead(sprite);
        removeHate(sprite);

        if (target == favTarget)
            setTarget(null);

        if (favTarget == sprite) {
            favTarget = null;
        }

        if (target == null)
            useBrain();
    }

    private void removeHate(Sprite sprite) {
        HateFrame f = searchSpriteHate(sprite);

        if (f != null) {
            blackList.remove(f);
        }

    }

    private void hateSprite(Sprite s, float dmg) {
        HateFrame f = searchSpriteHate(s);

        //not on blackList? - add new frame
        if (f == null && !evilWon) {
            //AngerFrame newHate = ;
            blackList.add(new HateFrame(dmg, s));
        } else if (f != null && !evilWon) {
            f.hateMore(dmg);
        }
    }

    /**
     * search Sprite in hate list
     *
     * @param s
     * @return
     */
    private HateFrame searchSpriteHate(Sprite s) {
        for (HateFrame a : blackList)
            if (a.sprite == s)
                return a;
        return null;
    }

    private Sprite getHatedSprite() {
        int index = -1;
        float max = 0;
        for (int i = 0; i < blackList.size(); i++) {
            HateFrame f = blackList.get(i);
            if (f.anger > max) {
                index = i;
                max = f.anger;
            }
        }

        if (index != -1) {
            return blackList.get(index).sprite;
        } else return null;
    }

    /**
     * frame for aggressive AI
     *
     * @author Zientara
     */
    private class HateFrame {
        private float anger;
        private Sprite sprite;

        public HateFrame(float anger, Sprite sprite) {
            super();
            this.anger = anger;
            this.sprite = sprite;
        }

        private void hateMore(float dmg) {
            anger += dmg;
        }

    }

    public void setEnemyId(int enemyId) {
        this.enemyId = enemyId;
    }

    public int getEnemyId() {
        return enemyId;
    }


    public void setExperience(float base, float level, float plus) {
        this.experience = (int) (base + level * plus);
    }

    public int getExperience() {
        return experience;
    }

    public void setAiType(int aiType) {
        target = null;
        this.aiType = aiType;
        useBrain();//refresh target
    }

    public int getAiType() {
        return aiType;
    }
}

