package pl.pwr.rafalz.arenagrozy.game.effects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import pl.pwr.rafalz.arenagrozy.game.GameObject;
import pl.pwr.rafalz.arenagrozy.game.sprites.Sprite;

/**
 * Point on ground. visual information about setted target/point on ground to attack/go.
 *
 * @author Evil
 */
public class Pointer extends GameObject {
    private int R;
    private RectF r;
    private Paint paint;
    private boolean visible;

    private Sprite target;

    //size after resize
    protected float wid;
    protected float hei;

    //plum effect ;]
    private float sinA;

    public Pointer(int x, int y, int r) {
        this.x = x;
        this.y = y;
        R = r;

        paint = new Paint();
        setColor(Color.GREEN);
        paint.setAntiAlias(true);

        super.width = (int) (wid = r * 2);
        super.height = (int) (hei = r);


        this.r = new RectF((int) ((x - wid / 2)), (int) ((y - hei / 2)),
                (int) ((x + wid / 2)), (int) ((y + hei / 2)));
    }

    @Override
    public void update(float dt) {
        if (visible) {
            //a - how much the size changes
            float a = 0.15f;
            float changeW = a * width;
            float changeH = a * height;
            float sin = (float) Math.sin(sinA * Math.PI);
            wid = width + changeW * sin;
            hei = height + changeH * sin;

            //how fast
            sinA += 2f * dt;
            if (sinA >= 2) {
                sinA -= 2;
            }
            if (target != null)
                setPosition(target.getX(), target.getY());

            r.set((int) ((x - wid / 2)), (int) ((y - hei / 2)),
                    (int) ((x + wid / 2)), (int) ((y + hei / 2)));
        }
    }

    public void draw(Canvas c) {
        if (visible) {
            c.drawOval(r, paint);
        }
    }

    public void setColor(int color) {
        paint.setColor(color);
        paint.setAlpha(120);
    }

    public void show() {
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move(float x, float y) {
        setPosition(x, y);
    }

    public int getColor() {
        return paint.getColor();
    }

    public Sprite getTarget() {
        return target;
    }

    public void setTarget(Sprite target) {
        this.target = target;
        int r = R;
        if (target != null)
            r = target.getR();

        if (r < R)
            r = R;

        super.width = (int) (wid = r * 2);
        super.height = (int) (hei = r);

    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void freeMemory() {
        target = null;
        r = null;
        paint = null;

    }

}
