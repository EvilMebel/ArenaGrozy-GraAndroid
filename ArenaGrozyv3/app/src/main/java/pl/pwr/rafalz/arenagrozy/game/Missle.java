package pl.pwr.rafalz.arenagrozy.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Round missle for Tasks - only special effect. Missle will fly to target and explode. Can have diffrent color, size.
 *
 * @author Zientara
 */
public class Missle extends GameMovObj {
    public static final float flightTime = 0.5f;

    private float r;
    private float rCurr1;
    private float rCurr2;

    private float yModifier;

    private int deltaAlpha = 400;
    private int alphaCurr = 120;

    private float aSin;
    private static final float rDelta = .9f;
    private final static float refreshDir = .15f;

    //my progress
    private float currRefreshDir;

    private boolean wentToDest;


    private GameObject target;

    //GRAPHICS
    private Paint paint;

    public Missle(float x, float y, float r, GameObject target) {
        super();
        this.x = x;
        this.y = y;
        this.r = r;
        this.target = target;

        yModifier = 0;
        paint = new Paint();
        paint.setAntiAlias(true);
        setColor(Color.GREEN);

        changeDestination(target.x, target.y, (float) distance(target) / 0.5f);
    }


    @Override
    public void update(float dt) {
        aSin += dt;
        if (aSin >= 2) {
            aSin -= 2;
        }

        rCurr1 = calculateR(aSin);
        rCurr2 = calculateR(aSin + 0.7F);

        float adt = dt;
        //now MOVE!
        if (!wentToDest) {
            float d = dt * speed; // my max possible d in dt
            float wantedD = (float) distance(dest_x, dest_y);

            // refreshing following target
            currRefreshDir += dt;
            if (currRefreshDir > refreshDir) {
                currRefreshDir = 0;
                changeDestination(target.x, target.y, speed);
            }

            if (d < wantedD) {// i cant make it in this dt
                x += speedX * dt;
                y += speedY * dt;
                adt = 0;
            } else { // i can do it and probably even more!
                float t1 = wantedD / speed;// minus current dt
                adt = dt - t1;

                x = target.x;
                y = target.y + 5;//5 for nice view- layers

                //dt -= mdt;
                speed = speedX = speedY = 0; // stop
                wentToDest = true;
            }

        }

        if (adt > 0) {
            //boom animation!
            r += r * 5 * dt;
            alphaCurr -= deltaAlpha * dt;
            if (alphaCurr <= 0) {
                alphaCurr = 0;
            }
            paint.setAlpha(alphaCurr);
        }
    }

    public void setColor(int color) {
        paint.setColor(color);
        paint.setAlpha(alphaCurr);
    }

    private float calculateR(float sin) {
        return (float) (r + Math.sin(sin * Math.PI) * r * rDelta);
    }

    @Override
    public void draw(Canvas c) {
        c.drawCircle(x, y - yModifier, rCurr1, paint);
        c.drawCircle(x, y - yModifier, rCurr2, paint);
    }

    @Override
    public boolean canBeDeleted() {
        return alphaCurr == 0;
    }

    @Override
    public void freeMemory() {

    }

    public void setyModifier(float yModifier) {
        this.yModifier = yModifier;
    }

    public float getyModifier() {
        return yModifier;
    }


    public void setscale(float missleScale) {
        r *= missleScale;
    }
}
