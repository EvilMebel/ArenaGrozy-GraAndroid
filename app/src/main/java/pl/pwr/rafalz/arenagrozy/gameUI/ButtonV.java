package pl.pwr.rafalz.arenagrozy.gameUI;

import pl.pwr.rafalz.arenagrozy.game.LifeBar;
import pl.pwr.rafalz.arenagrozy.game.RunnableInerface;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import pl.pwr.rafalz.arenagrozy.view.GameView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Standard button for game UI. Button with animation when click
 *
 * @author Zientara
 */
public class ButtonV implements RunnableInerface {
    protected Rect r;
    protected Rect src;
    protected Paint paint;
    protected Paint paintOff;
    protected boolean is_on;
    protected Bitmap bmp_on;

    private int x;
    private int y;
    private boolean plum;
    //standard size
    private int widthF;
    private int heightF;
    //size on screen
    protected float width;
    protected float height;

    private float sinA;


    private int deltaAlpha;
    protected int currAlpha;

    private LifeBar lifebar = null;

    public ButtonV(int x, int y, int wid, int hei, int bmpId) {
        this(x, y, wid, hei);

        bmp_on = BitmapFactory.decodeResource(GameView.game.get().getResources(), bmpId);
        src = new Rect(0, 0, bmp_on.getWidth(), bmp_on.getHeight());
    }

    ButtonV(int x, int y, int wid, int hei) {
        this.x = x;
        this.y = y;

        paint = new Paint();
        paintOff = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(hei / 2);
        paint.setAntiAlias(true);

        is_on = false;

        width = widthF = wid;
        height = heightF = hei;

        r = new Rect((int) ((x - width / 2)), (int) ((y - height / 2)),
                (int) ((x + width / 2)), (int) ((y + height / 2)));
        src = new Rect();

        //fade out animation
        deltaAlpha = 0;
        currAlpha = 255;
    }

    @Override
    public void update(float dt) {
        if (plum) {
            //a - how much the size changes
            float a = 0.15f;
            if (sinA > 1)
                a = 0.1f;

            float changeW = a * widthF;
            float changeH = a * heightF;


            float sin = (float) Math.sin(sinA * Math.PI);

            width = widthF + changeW * sin;
            height = heightF + changeH * sin;

            //how fast
            sinA += 3f * dt;
            if (sinA >= 2) {
                plum = false;
                sinA = 0;
            }

            r.set((int) ((x - width / 2)), (int) ((y - height / 2)),
                    (int) ((x + width / 2)), (int) ((y + height / 2)));
        }

        if (deltaAlpha != 0) {
            currAlpha -= deltaAlpha * dt;

            if (currAlpha <= 0) {
                deltaAlpha = 0;
                currAlpha = 0;
            }
        }

    }

    public void draw(Canvas c) {

        c.drawBitmap(bmp_on, src, r, paint);

        if (!is_on && currAlpha > 0) {
            paintOff.setColor(Color.GRAY);
            paintOff.setAlpha(getShadeAlpha());
            c.drawRect(r, paintOff);
        }


        paint.setAlpha(currAlpha);

        if (lifebar != null) {
            lifebar.draw(c);
            lifebar.setAlpha(currAlpha);
        }
    }

    /**
     * prepare alpha setting for shadow
     *
     * @return
     */
    protected int getShadeAlpha() {
        int a = currAlpha - 80;
        if (a < 0)
            a = 0;
        return a;
    }

    public void fadeOutAnimation() {
        deltaAlpha = 500;
    }

    /**
     * set button image file
     *
     * @param bmpId
     */
    public void setBmp(int bmpId) {
        bmp_on = BitmapFactory.decodeResource(GameView.game.get().getResources(), bmpId);
        src = new Rect(0, 0, bmp_on.getWidth(), bmp_on.getHeight());

        r = new Rect((int) ((x - width / 2)), (int) ((y - height / 2)),
                (int) ((x + width / 2)), (int) ((y + height / 2)));
    }

    public ButtonV setPaintFilter(int color) {
        LightingColorFilter darkFilter = new LightingColorFilter(color, 50);
        paint.setColorFilter(darkFilter);

        return this;
    }

    @Override
    public boolean canBeDeleted() {
        return false;
    }

    public boolean isClicked(float ax, float ay) {
        float x = (ax);
        float y = (ay);
        if (r.left < x && r.right > x &&
                r.top < y && r.bottom > y) {
            plum = true;
            return true;
        }
        return false;
    }

    public String getPosition() {
        return "x = " + x + " y = " + y;
    }

    public void forceClick() {
        plum = true;
    }

    /**
     * show wave effect of button
     */
    public void plum() {
        plum = true;
    }

    /**
     * also sets the position of lifebar
     *
     * @param lifebar
     */
    public void setLifebar(LifeBar lifebar) {
        this.lifebar = lifebar;
        this.lifebar.setPosition(x, y + height / 2 + lifebar.getHeight() / 2);
    }

    public LifeBar getLifebar() {
        return lifebar;
    }

    public Rect getR() {
        return r;
    }

    public void setR(Rect r) {
        this.r = r;
    }

    public void focus() {
        is_on = true;
    }

    public void unfocus() {
        is_on = false;
    }

    public boolean isIs_on() {
        return is_on;
    }

    public void setIs_on(boolean is_on) {
        this.is_on = is_on;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * recycle bitmap
     */
    @Override
    public void freeMemory() {
        Toolbox.freeMemory(bmp_on);
    }
}
