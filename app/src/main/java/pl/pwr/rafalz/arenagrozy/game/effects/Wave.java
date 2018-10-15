package pl.pwr.rafalz.arenagrozy.game.effects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import pl.pwr.rafalz.arenagrozy.game.GameObject;

/**
 * Wave on ground - special effect
 *
 * @author Zientara
 */
public class Wave extends GameObject {
    private float r;
    private float sizeDelta;// speed of changing
    private float plusSize;// current size

    private int alpha;
    private int alphaDelta;// speed of changing
    private Paint paint;
    private RectF rect;
    private boolean visible;

    public Wave(float r) {
        super();
        this.r = r;
        this.alpha = 160;
        this.alphaDelta = 400;
        sizeDelta = r;
        plusSize = r;
        visible = true;

        rect = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);// DMG!
    }

    @Override
    public void draw(Canvas c) {
        if (visible)
            rect.set(x - plusSize, y - plusSize / 2, x + plusSize, y + plusSize / 2);
        c.drawOval(rect, paint);
    }

    @Override
    public void update(float dt) {
        if (visible) {
            plusSize += sizeDelta * dt;
            alpha -= alphaDelta * dt;

            if (alpha < 0) {
                alpha = 0;
                visible = false;
            }
            paint.setAlpha(alpha);
        }
    }

    /**
     * if not visible can be deleted
     */
    @Override
    public boolean canBeDeleted() {
        return !visible;
    }

    @Override
    public void freeMemory() {

    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public float getSizeDelta() {
        return sizeDelta;
    }

    /**
     * value in %
     *
     * @param sizeDelta
     */
    public void setSizeDelta(float sizeDelta) {
        this.sizeDelta = r * sizeDelta;
    }

    public int getAlphaDelta() {
        return alphaDelta;
    }

    public void setAlphaDelta(int alphaDelta) {
        this.alphaDelta = alphaDelta;
    }

}
