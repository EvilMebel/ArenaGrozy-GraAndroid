package pl.pwr.rafalz.arenagrozy.gameUI;

import pl.pwr.rafalz.arenagrozy.data.TaskDB;
import pl.pwr.rafalz.arenagrozy.game.Sprite;
import pl.pwr.rafalz.arenagrozy.game.Task;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

/**
 * Button for spells. When spell is used, button needs time for regenerating - shows animation.
 *
 * @author Zientara
 */
public class SpellButton extends ButtonV {
    private String tag = getClass().getName();

    private float time_total; //full time of regeneration
    private float time_load; //ile milisekund juz minelo
    private Task task;
    private int lol;
    private boolean need_target; //mowi czy mozna uzyc czaru zaleznie od RANGE MODE
    private boolean have_target;

    public SpellButton(int x, int y, int wid, int hei, int spell, Sprite parent, float level) {
        super(x, y, wid, hei);
        task = TaskDB.getTask(spell, parent, level);
        setBmp(TaskDB.getTaskIcon(spell));
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        this.time_total = task.getLoading();//na start wszystkie czary naladowane
        time_load = 0;
        is_on = true;

        currAlpha = 255;

        int r = task.getRange_mode();
        if (r == Sprite.RANGE_CLOSE || r == Sprite.RANGE_FAR)
            need_target = true;
        else
            need_target = false;
    }

    @Override
    public void draw(Canvas canvas) {
        //dont call super - override all

        paint.setAlpha(currAlpha);
        canvas.drawBitmap(bmp_on, src, r, paint);


        if (!is_on) {
            //count %
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(getShadeAlpha());
            int proc = (int) (((float) time_load / (float) time_total) * 100);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);

            path.moveTo(r.left + width / 2, r.top + height / 2);//srodek
            path.lineTo(r.left + width / 2, r.top);//gora


            //show progress of loading
            if (proc >= 12.5) {
                path.lineTo(r.left, r.top);//lewo gora
                if (proc >= 37.5) {
                    path.lineTo(r.left, r.bottom);//lewo gora
                    if (proc >= 62.5) {
                        path.lineTo(r.right, r.bottom);//prawo dol
                        if (proc >= 87.5) {
                            path.lineTo(r.right, r.top);//prawo gora
                            if (proc == 100) {
                                path.lineTo(r.left + width / 2, r.top);// gora
                            } else {//less than 100%
                                path.lineTo(r.left + width - ((width / 2) * ((float) (proc - 87.5) / 12.5f)), r.top);//lewo gora
                            }
                        } else {//less than 87%
                            path.lineTo(r.right, r.top + height - ((height) * ((float) (proc - 62.5) / 25f)));//lewo gora
                        }
                    } else {//less than 62%
                        path.lineTo(r.left + ((width) * ((float) (proc - 37.5) / 25f)), r.bottom);//lewo gora
                    }
                } else {//less than 37%
                    path.lineTo(r.left, r.top + ((height) * ((float) (proc - 12.5) / 25f)));//lewo gora
                }
            } else {//less than 12%
                path.lineTo(r.left + width / 2 - ((width / 2) * ((float) proc / 12.5f)), r.top);//lewo gora
            }
            //close path
            path.lineTo(r.left + width / 2, r.top + height / 2);//srodek
            path.close();
            canvas.drawPath(path, paint);
        } else if (need_target && !have_target) {
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(getShadeAlpha());
            canvas.drawRect(r, paint);

        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (!is_on)//button has already been pressed
        {
            time_load -= dt;
            if (time_load <= 0) {
                is_on = true;
                time_load = time_total;
            }
        }
    }

    /**
     * use spell and start regenerating animation
     */
    public boolean useSpell() {
        if (is_on) {
            if (!need_target) {
                time_load = time_total;
                is_on = false;
                return true;
            } else if (need_target && have_target) {
                time_load = time_total;
                is_on = false;
                Log.d(tag, "iso = false");
                return true;
            }

        }
        return false;
    }

    public Task getSpell() {
        return task;
    }

    public boolean isHave_target() {
        return have_target;
    }

    public void setHave_target(boolean have_target) {
        this.have_target = have_target;
    }


    @Override
    public void freeMemory() {
        super.freeMemory();
        if (task != null)
            task.freeMemory();
    }


}
