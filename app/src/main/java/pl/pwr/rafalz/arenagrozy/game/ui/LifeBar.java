package pl.pwr.rafalz.arenagrozy.game.ui;

import pl.pwr.rafalz.arenagrozy.game.GameObject;
import pl.pwr.rafalz.arenagrozy.game.sprites.Sprite;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Life bar. Can be attached to GameObject.
 *
 * @author Evil
 */
public class LifeBar extends GameObject {
    // life bar
    private RectF lifeBarFrame;

    private final static float hideDelay = 10f;
    private final static int hideAlphaSpeed = 400;
    private float currHideDelay;
    private int deltaAlpha;
    protected int currAlpha;

    private static final int lifeFrameColor = Color.BLACK;
    private static final int barColorBg = Color.parseColor("#BE2625");// red
    private static final int barColor = Color.parseColor("#66CD00");// green
    private RectF lifeBarHP;
    private Paint framePaint;
    private Paint barPaint;
    private Paint barBgPaint;
    private float maxHP;
    private float HP;
    private Sprite anchor;
    private float yModified;
    private float padd;// frame padding
    private float barWidth; // depends on current hp

    private boolean visible = true;

    public LifeBar(Sprite anchor) {
        super();
        HP = this.maxHP = anchor.getHpStart();
        this.anchor = anchor;
        framePaint = new Paint();
        framePaint.setColor(lifeFrameColor);

        //fade out animation
        deltaAlpha = 0;
        currAlpha = 0;

        barPaint = new Paint();
        barPaint.setColor(barColor);

        barBgPaint = new Paint();
        barBgPaint.setColor(barColorBg);

        lifeBarFrame = new RectF();
        lifeBarHP = new RectF();

        setSize(Toolbox.spriteWidth / 3 * 2);
        setAlpha(currAlpha);
        yModified = anchor.getHeight() * 1f;// defoult value
    }

    @Override
    public void update(float dt) {
        if (currHideDelay < hideDelay) {
            currHideDelay += dt;
        } else if (deltaAlpha == 0 && currHideDelay >= hideDelay && HP == maxHP)
            deltaAlpha = hideAlphaSpeed;

        if (deltaAlpha != 0) {
            currAlpha -= deltaAlpha * dt;

            if (currAlpha <= 0) {
                deltaAlpha = 0;
                currAlpha = 0;
            } else if (currAlpha >= 255) {
                deltaAlpha = 0;
                currAlpha = 255;
            }
            setAlpha(currAlpha);
        }
    }

    @Override
    public void draw(Canvas c) {
        if (visible) {
            if (anchor != null) {
                y = anchor.getY() - yModified;
                x = anchor.getX();
            }
            lifeBarFrame.set(x - width, y - height, x + width, y + height);
            lifeBarHP.set(x - width + padd, y - height + padd,
                    x + width - padd, y + height - padd);
            // Log.d(tag, "padd = " + padd);
            c.drawRect(lifeBarFrame, framePaint);
            c.drawRect(lifeBarHP, barBgPaint);

            if (HP > 0) {
                lifeBarHP.set(x - width + padd, y - height + padd, x - width
                        + padd + barWidth, y + height - padd);
                c.drawRect(lifeBarHP, barPaint);
            }
        }

    }

    @Override
    public void freeMemory() {

    }

    public void setSize(int awidth) {
        width = awidth / 2;
        height = width / 4;
        padd = height / 2;
        refresh();
    }

    public void hide() {
        deltaAlpha = -hideAlphaSpeed;
    }

    private void refresh() {
        float prop = HP / maxHP;
        float fullWidth = (width - padd) * 2;
        barWidth = prop * fullWidth;
    }

    public void damage(float dmg) {
        HP -= dmg;
        if (HP < 0)
            HP = 0;

        refresh();
    }

    public float getMaxHP() {
        return maxHP;
    }

    /**
     * refresh life bar
     *
     * @param maxHP
     */
    public void setMaxHP(float maxHP) {
        HP = this.maxHP = maxHP;
        refresh();
    }

    public float getHP() {
        return HP;
    }

    public void setHP(float hP) {
        if (this.HP != hP) {
            //show life bar if is invisible
            deltaAlpha = -hideAlphaSpeed;
            currHideDelay = 0;
        }

        HP = hP;
        if (HP < 0)
            HP = 0;
        refresh();
    }

    public GameObject getAnchor() {
        return anchor;
    }

    public void setAnchor(Sprite anchor) {
        this.anchor = anchor;

        if (anchor == null)
            yModified = 0;
    }

    public float getyModified() {
        return yModified;
    }

    public void setyModified(float yModified) {
        this.yModified = yModified;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setAlpha(int currAlpha) {
        framePaint.setAlpha(currAlpha);
        barPaint.setAlpha(currAlpha);
        barBgPaint.setAlpha(currAlpha);

    }

}
