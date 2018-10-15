package pl.pwr.rafalz.arenagrozy.game.effects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import pl.pwr.rafalz.arenagrozy.game.GameObject;

/**
 * special effect - fill all screen with wanted color
 *
 * @author Zientara
 */
public class FillScreen extends GameObject {

    private int startAlpha;
    private int deltaAlpha;
    private Paint paint;
    private boolean visible = true;

    public FillScreen() {
        super();
        startAlpha = 120;
        deltaAlpha = 400;
        paint = new Paint();
        setColor(Color.RED);
    }

    @Override
    public void update(float dt) {
        if (visible) {
            startAlpha -= deltaAlpha * dt;
            if (startAlpha <= 0) {
                visible = false;
            }
        }
    }

    @Override
    public void draw(Canvas c) {
        if (visible) {
            paint.setAlpha(startAlpha);
            c.drawColor(paint.getColor());

        }
    }

    public void setColor(int color) {
        paint.setColor(color);
        paint.setAlpha(startAlpha);
    }

    @Override
    public boolean canBeDeleted() {
        return !visible;
    }

    @Override
    public void freeMemory() {

    }

    public int getStartAlpha() {
        return startAlpha;
    }

    public void setStartAlpha(int startAlpha) {
        this.startAlpha = startAlpha;
    }

    public int getDeltaAlpha() {
        return deltaAlpha;
    }

    public void setDeltaAlpha(int deltaAlpha) {
        this.deltaAlpha = deltaAlpha;
    }
}
