package pl.pwr.rafalz.arenagrozy.game.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import pl.pwr.rafalz.arenagrozy.game.GameObject;
import pl.pwr.rafalz.arenagrozy.game.sprites.Sprite;
import pl.pwr.rafalz.arenagrozy.game.sprites.Stats;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;

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
    private final Stats stats;
    private RectF lifeBarHP;
    private Paint framePaint;
    private Paint barPaint;
    private Paint barBgPaint;
    private Sprite anchor;
    private float yModified;
    private float padd;// frame padding
    private float barWidth; // depends on current hp

    private boolean visible = true;

    public LifeBar(Sprite anchor, Stats stats) {
        super();
        this.anchor = anchor;
        this.stats = stats;
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
        } else if (deltaAlpha == 0 && currHideDelay >= hideDelay && stats.isFullHp())
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

            if (stats.getHp() > 0) {
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

    public void refresh() {
        float prop = stats.getHpProc();
        float fullWidth = (width - padd) * 2;
        barWidth = prop * fullWidth;
    }

    public void setHP(float hP) {
        deltaAlpha = -hideAlphaSpeed;
        currHideDelay = 0;
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
