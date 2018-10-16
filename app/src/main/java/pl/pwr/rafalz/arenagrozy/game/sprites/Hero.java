package pl.pwr.rafalz.arenagrozy.game.sprites;

import pl.pwr.rafalz.arenagrozy.game.ui.LifeBar;
import pl.pwr.rafalz.arenagrozy.game.Task;

/**
 * class for heroes.
 *
 * @author Zientara
 */
public class Hero extends Sprite {
    public final static boolean SELF_DEFENCE_MODE = true;// annoying
    private final String tag = getClass().getName();// test
    private int whichHero;
    private LifeBar buttonLifeBar;

    public Hero(float x, float y, int IDwalk, int walkFrames, int IDstay,
                int stayFrames, float scale, int whichHero, Stats stats) {
        super(IDwalk, walkFrames, IDstay, stayFrames, scale, stats);
        this.x = x;
        this.y = y;
        this.whichHero = whichHero;
    }

    /**
     * hit this sprite. If Enemy dont have targer or point on screen to
     * attack/go then object will attack beater.
     */
    @Override
    public float hit(Task hitter, float stunT) {
        float dmg = super.hit(hitter, stunT);

        if (target == null && speed == 0 && Hero.SELF_DEFENCE_MODE) {
            // self defense
            setTarget(hitter.getParent());
        }

        stun(stunT);

        return dmg;
    }

    @Override
    public void setHp(float hp) {
        super.setHp(hp);
        if (buttonLifeBar != null)
            buttonLifeBar.setHP(hp);
    }

    public int getWhichHero() {
        return whichHero;
    }

    public void setWhichHero(int whichHero) {
        this.whichHero = whichHero;
    }

    public void setButtonLifeBar(LifeBar buttonLifeBar) {
        this.buttonLifeBar = buttonLifeBar;
    }

    public LifeBar getButtonLifeBar() {
        return buttonLifeBar;
    }

    /**
     * use only for ending when all enemies are defeated!
     */
    public void showLifeBar() {
        setHp(stats.getHp() + 0.0001f);// small update for showing lifebar

    }

    public boolean isStunned() {
        return status == STATUS_STUN;
    }
}
