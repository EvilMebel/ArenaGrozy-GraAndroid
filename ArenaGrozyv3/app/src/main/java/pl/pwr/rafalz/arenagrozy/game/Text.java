package pl.pwr.rafalz.arenagrozy.game;

import pl.pwr.rafalz.arenagrozy.tools.Toolbox;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;

/**
 * Visualization for dmg, and texts on screen. By default have setted fade out
 * animation and scaling. Can be change to static text.
 *
 * @author Zientara
 */
public class Text extends GameObject {

    private Paint paint;
    private String text;
    private int startAlpha;
    private int deltaAlpha;
    private float startSize;
    private float deltaSize;
    private boolean visible;

    public Text(float text) {
        init(Integer.toString((int) Math.abs(text)), Toolbox.screenHeight / 15);
    }

    public Text(String text, int startSize) {
        super();
        init(text, startSize);
    }

    public Text(String text) {
        super();
        init(text, this.startSize = Toolbox.screenHeight / 15);
    }

    private void init(String text, float startSize) {
        this.text = text;
        this.startSize = startSize;

        visible = true;
        startAlpha = 200;
        deltaAlpha = 300;
        deltaSize = this.startSize * 0.5f;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(this.startSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(Color.RED);
    }

    @Override
    public void update(float dt) {
        if (visible) {
            startSize += deltaSize * dt;
            startAlpha -= deltaAlpha * dt;

            if (startAlpha <= 0) {
                visible = false;
                startAlpha = 0;
            }

            paint.setTextSize(startSize);
            paint.setAlpha(startAlpha);
        }
    }

    @Override
    public void draw(Canvas c) {
        if (visible)
            c.drawText(text, x, y, paint);
    }

    @Override
    public void freeMemory() {

    }

    @Override
    public void setPosition(GameObject go) {
        super.setPosition(go);
        if (go != null)
            y -= go.getHeight() * Toolbox.getRandom(.4f, .7f);
        x -= go.getWidth() * Toolbox.getRandom(-.3f, .3f);
    }

    public void setColor(int green) {
        paint.setColor(green);
        paint.setAlpha(startAlpha);
    }

    public void setDeltaAlpha(int deltaAlpha) {
        this.deltaAlpha = Math.abs(deltaAlpha);
    }

    public void setDeltaSize(float proc) {
        deltaSize = this.startSize * proc;
    }

    public void setTextPosition(Align align) {
        paint.setTextAlign(align);
    }

    public int getTextSize() {
        return (int) paint.getTextSize();
    }

    public void setText(String text) {
        this.text = text;
    }

}
