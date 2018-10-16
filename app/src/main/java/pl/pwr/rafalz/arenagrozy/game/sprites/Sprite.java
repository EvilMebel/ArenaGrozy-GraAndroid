package pl.pwr.rafalz.arenagrozy.game.sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pl.pwr.rafalz.arenagrozy.data.TaskDB;
import pl.pwr.rafalz.arenagrozy.game.GameMovObj;
import pl.pwr.rafalz.arenagrozy.game.Task;
import pl.pwr.rafalz.arenagrozy.game.effects.Pointer;
import pl.pwr.rafalz.arenagrozy.game.ui.LifeBar;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import pl.pwr.rafalz.arenagrozy.view.GameView;

/**
 * Class for all characters on screen.
 *
 * @author Evil
 */
public abstract class Sprite extends GameMovObj {
    final Stats stats;
    private final String tag = getClass().getName();
    static final boolean DEBUG_MODE = false;

    /*
     * STATIC INFO
     */
    private final static int STATUS_WALK = 0;
    private final static int STATUS_STAY = 1;
    private final static int STATUS_DEAD = 2;
    private final static int STATUS_TASK = 3;//update hit action
    final static int STATUS_STUN = 4;

    private static final float CORR_HEI = 0.90f;

    // types of spell range
    public final static int RANGE_FAR = 0;//hit target from distance
    public final static int RANGE_CLOSE = 1;//need to be close to hit target
    public final static int RANGE_LOCAL = 2;//hit with range R
    public final static int RANGE_GLOBAL = 3;//hit on whole screen


    //animations
    private Bitmap[] walkL;
    private Bitmap[] walkR;
    private int bmpIdWalk;

    private Bitmap[] stayL;
    private Bitmap[] stayR;
    private int bmpIdStay;

    protected Paint paint;
    private float frame;

    //height where are feets of character
    private boolean lookLeft;
    private float scale;

    private RectF shadow;
    private Paint shadowPaint;
    private int shadowR;
    protected int r;

    //life bar
    private LifeBar lifeBar;

    // targetting, healing
    private Pointer pointer;
    protected Sprite target;

    protected int status;

    protected List<Task> tasks;
    protected Task current_task;

    protected Task task_hit;
    protected Task task_heal;

    protected boolean healer;


    //REFRESHING SETTING
    private final static float refreshDir = .15f;
    private final static float HIT_INTERVAL = 1.3f;
    private final static int deadDeltaAlpha = 300;

    //my progress
    private float currRefreshDir;
    private float currHitInterval;
    private int currentDeadAlpha = 255;

    private Path shockStar;
    private float starOffsetY;
    private float starRotation;
    private Paint paintStar;

    //stun info
    private float stunTime;
    private float currStunTime;

    private Rect dstBmp;

    public Sprite(int idWalk, int walkFrames, int idStay, int stayFrames,
                  float scale, Stats stats) {
        super();
        this.scale = scale;
        bmpIdWalk = idWalk;
        bmpIdStay = idStay;
        this.stats = stats;
        paint = new Paint();
        shadowPaint = new Paint();
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAlpha(30);
        paintStar = new Paint();
        paintStar.setColor(Color.YELLOW);
        paintStar.setAlpha(190);
        paintStar.setAntiAlias(true);

        standardSpeed = Toolbox.screenHeight / 6;//def value

        walkL = Toolbox.getFramesFromBitmap(idWalk, walkFrames);
        walkR = Toolbox.getMirroredFrames(idWalk);
        stayL = Toolbox.getFramesFromBitmap(idStay, stayFrames);
        stayR = Toolbox.getMirroredFrames(idStay);

        width = (int) (scale * Toolbox.spriteWidth);
        height = (int) (scale * Toolbox.spriteHeight);
        dstBmp = new Rect();

        r = width / 3;
        shadowR = width / 4;

        starOffsetY = height * 0.9f;
        shockStar = new Path();

        shadow = new RectF();
        status = STATUS_STAY;

        //list of to do Tasks
        tasks = new ArrayList<Task>();
        //attach lifebar
        lifeBar = new LifeBar(this, stats);
    }

    @Override
    public void draw(Canvas c) {
        if (currentDeadAlpha > 0) {
            Bitmap bmp = null;
            switch (status) {
                case STATUS_WALK:
                    if (lookLeft)
                        bmp = walkL[(int) frame];
                    else
                        bmp = walkR[(int) frame];
                    break;
                default:
                case STATUS_STAY:
                    if (lookLeft)
                        bmp = stayL[(int) frame];
                    else
                        bmp = stayR[(int) frame];
                    break;
                case STATUS_TASK:
                    if (current_task != null)
                        bmp = current_task.getCurrentBmp();
                    break;
            }

            // shadow
            shadow.set(x - shadowR, y - shadowR / 2, x + shadowR, y + shadowR / 2);
            c.drawOval(shadow, shadowPaint);

            //character
            int xbmp = (int) (x - width / 2);
            int ybmp = (int) ((int) (y - height * CORR_HEI));
            dstBmp.set(xbmp, ybmp, xbmp + width, ybmp + height);

            if (bmp != null)
                c.drawBitmap(bmp, null, dstBmp, paint);

            if (status == STATUS_STUN) {
                prepareShockStar();
                c.drawPath(shockStar, paintStar);
            }
            // lifebar
            if (status == STATUS_DEAD)
                lifeBar.setAlpha(currentDeadAlpha);
            lifeBar.draw(c);

            if (DEBUG_MODE)
                c.drawText("stat " + status, x, y + height / 2, paint);
        }
    }

    @Override
    public void update(float adt) {
        frame += adt * 8; // 6 frames per second
        frame = frame % 8;

        float radiansPSec = (float) Math.PI;
        starRotation = (starRotation + radiansPSec * adt) % 360;

        float dt = adt;

        while (dt > 0) {
            switch (status) {
                case STATUS_STAY:
                    dt = updateStay(dt);

                    break;
                case STATUS_DEAD:
                    //dead animation - alpha
                    frame = 0;//stop animation
                    setHp(0);
                    currentDeadAlpha -= deadDeltaAlpha * dt;
                    if (currentDeadAlpha < 0)
                        currentDeadAlpha = 0;
                    paint.setAlpha(currentDeadAlpha);
                    dt = 0;
                    break;
                case STATUS_WALK:

                    float d = dt * speed; //my max possible d in dt
                    float maxD = (float) distance(dest_x, dest_y);
                    float wantedD;
                    if (target == null) {
                        wantedD = maxD;
                    } else {
                        wantedD = maxD - r - target.r;
                        if (wantedD < 0)
                            wantedD = 0;

                        //refreshing following target
                        currRefreshDir += dt;
                        if (currRefreshDir > refreshDir) {
                            currRefreshDir = 0;
                            changeDestination(target, speed);
                        }
                    }


                    if (d < wantedD) {//i cant make it in this dt
                        x += speedX * dt;
                        y += speedY * dt;
                        dt = 0;
                    } else { //i can do it and probably even more!
                        //minus current dt
                        float mdt = wantedD / speed;
                        x += mdt * speed;
                        y += mdt * speed;

                        dest_x = x;
                        dest_y = y;
                        dt -= mdt;
                        speed = speedX = speedY = 0;
                        if (pointer != null && target == null)
                            pointer.hide();
                        status = STATUS_STAY;
                        if (target != null) {
                            setTarget(target);//refresh target ascions
                        }
                    }
                    break;
                case STATUS_TASK:
                    dt = updateTaskStatus(dt);
                    break;

                case STATUS_STUN:
                    float wantedDT = (stunTime - currStunTime);

                    if (wantedDT > dt) {
                        currStunTime += dt;
                        dt = 0;
                    } else {
                        //end of stun
                        currStunTime = 0;
                        status = STATUS_STAY;
                        dt -= wantedDT;
                    }

                    break;
            }
        }
        lifeBar.update(adt);
    }

    /**
     * update behaviour on STAY status
     */
    private float updateStay(float adt) {
        float dt = adt;
        if (canTakeDefaultTask() && target == null) {
            dt = 0; //nothing to do there - just stay
            useBrain();
        } else {
            // interval
            float wantedDT = HIT_INTERVAL - currHitInterval;

            if (wantedDT > dt) {// i cant make it in this dt
                currHitInterval += dt;
                dt = 0;
            } else { // i can do it and probably even more!
                dt -= wantedDT;
                currHitInterval = 0;
                if (target != null || tasks.size() > 0)
                    takeNextTask();
            }
        }
        return dt;
    }

    /**
     * void for Enemy to attack a new target! Don't forget override
     */
    protected void useBrain() {
    }

    /**
     * update behaviour on updating Task (status TASK)
     *
     * @param dt
     * @return
     */
    private float updateTaskStatus(float dt) {
        if (current_task == null) {
            takeNextTask();
        }
        boolean permission = false;
        if (current_task != null) {
            int range = current_task.getRange_mode();

            if (current_task.isNeed_target() && target != null) {
                lookAtTarget(target);
                if (range == RANGE_FAR) {
                    permission = true; //fire!
                } else {
                    if (distance(target) > target.r + r * 1.5) {
                        changeDestination(target, current_task.getRunSpeed());
                        return 0; //follow target
                    } else {
                        permission = true;
                    }
                }
            }

            if (range == RANGE_LOCAL || range == RANGE_GLOBAL) {
                permission = true;
            }

            if (permission) {
                float delta = current_task.update(dt);
                if (current_task != null && current_task.isCompleted()) {
                    cancelCurrentTask();
                }
                return delta;
            } else {
                cancelCurrentTask();
                return 0;
            }
        }
        return 0;//I did nothing!
    }

    /**
     * stop current task and change status to STAY
     */
    private void cancelCurrentTask() {
        if (current_task != null) {
            current_task.cancelTask();
            current_task = null;
            status = STATUS_STAY;
        }
    }

    /**
     * Only sprite on screen can be hitted because stun can stop sprite outside of screen - then we cant point a target.
     *
     * @param hitter
     * @param stunT
     * @return
     */
    public float hit(Task hitter, float stunT) {
        if (isOnScreen()) {
            // hitter - i'm the one who knocks
            Sprite s = hitter.getParent();
            //hitter stats
            float power = hitter.getPower();
            float strMod = 1 + s.stats.getStr() / 100f;

            //my stats
            float defMod = (1 - this.stats.getDef() / 100);
            float dmg = power * strMod * defMod;

            Log.d(tag, "HIT! power:" + power + " strMod:" + strMod + " defMod:" + defMod + " finMod:" + strMod * defMod + " taskId:" + hitter.getTaskId());
            float rand = Toolbox.getRandom(0, 100);
            if (rand > 90) {
                //critical
                dmg *= Toolbox.getRandom(1.9f, 2.1f);
            } else if (rand < 2) {
                dmg = 0;
                //miss
            } else {
                //normal hit
                dmg *= Toolbox.getRandom(0.9f, 1.1f);
            }

            printStats(hitter);
            if (dmg < 0)
                dmg = 0;

            setHp(stats.getHp() - dmg);
            if (stats.getHp() < 1) {
                setHp(0);
                if (pointer != null) {
                    pointer.hide();
                    pointer = null;
                }
                GameView.game.get().spriteIsDead(this);
            }

            if (stats.getHp() > stats.getHpStart())
                setHp(stats.getHpStart());

            stun(stunT);

            return dmg;
        } else
            return 0;
    }

    /**
     * cancels current task and changes status to STUN - shock
     */
    protected void stun(float stunTime) {
        if (stats.getHp() > 0 && stunTime > 0) {
            Log.d(tag, "START of stun");
            cancelCurrentTask();
            this.stunTime = stunTime;
            status = STATUS_STUN;
        }
    }

    /**
     * heal this sprite
     */
    public float heal(Task healer) {
        Sprite s = healer.getParent();
        //get stats of healer - not me
        float heal = healer.getPower() * (1 + s.stats.getStr() / 100);

        float rand = Toolbox.getRandom(0, 100);
        if (rand > 90) {
            Log.d(tag, "HEAL! CRITICAL");
            heal *= Toolbox.getRandom(1.9f, 2.1f);
        } else {
            heal *= Toolbox.getRandom(0.9f, 1.1f);
        }

        //special condition for skill1 hero 3
        if (healer.getTaskId() == TaskDB.SPECIAL_H3_S1 && this instanceof Hero && healer.getParent() == this) {
            //heal caster bonus
            Log.d(tag, "Special heal condition!");
            heal *= 2;
        }

        if (heal < 1)
            heal = 0;

        setHp(stats.getHp() + heal);

        if (stats.getHp() > stats.getHpStart())
            setHp(stats.getHpStart());

        return heal;
    }

    /**
     * hit - hitter , me - this, t - task power
     */
    public void printStats(Task h) {
        if (Toolbox.TEST_MODE) {
            Sprite hit = h.getParent();
            String s = "PS -knocker! " + hit.statsInfo() + " Victim - me ;( "
                    + this.statsInfo() + " Task power = " + h.getPower();

            Log.d(tag, s);
        }
    }


    /**
     * set a point where object should go. Id follow_target is false, Sprite will reject current target
     */
    public void changeDestination(float dest_x, float dest_y, float aspeed,
                                  boolean follow_target) {

        status = STATUS_WALK;
        if (!follow_target) {
            target = null; // cancel following
            if (pointer != null)
                pointer.setTarget(null);
        }

        changeDestination(dest_x, dest_y, aspeed);

        if (pointer != null) {
            pointer.setPosition(this.dest_x, this.dest_y);
            pointer.show();
        }

        refreshAnimDir();
    }

    /**
     * go to Sprite with specified speed
     */
    public void changeDestination(Sprite s, float speed) {
        if (s != null) {
            target = s;

            if (pointer != null) {
                pointer.show();
                pointer.setTarget(target);
            }
            changeDestination((int) (s.x), (int) s.y, speed, true);
        } else if (Toolbox.TEST_MODE)
            Log.d("changeDest", "Sprite do celu == null ;/");
    }

    private void refreshAnimDir() {
        if (speedX < 0)
            lookLeft = true;
        else if (speedX > 0)
            lookLeft = false;
    }

    /**
     * can i reach target to hit him from close distance?
     */
    protected boolean isTargetInRange() {
        if (target != null) {
            return distance(target) <= r + target.r;
        }
        return false;
    }

    /**
     * use special attack - for hero
     */
    public void useSpell(Task t) {
        status = STATUS_TASK;
        tasks.add(t);

        if (current_task != null && (current_task == task_hit || current_task == task_heal)) {
            current_task.cancelTask();//cancel standard task and take a better one
            //player don't want to wait forever
            current_task = null;
        }

        if (current_task != null && current_task.isNeed_target() && target != null) {
            current_task.setTarget(target);
        } else if (current_task != null && !current_task.isNeed_target()) {
            if (pointer != null)
                pointer.hide();
        }

    }


    /**
     * sets current task. If to do list of task is not empty - take task. If
     * list is empty then take default hit or heal task.
     */
    private void takeNextTask() {
        if (current_task == null && tasks.size() > 0) {
            current_task = tasks.get(0);
            current_task.setTarget(target);
            tasks.remove(current_task);// remove this from todo tasks
            if (canMove())
                status = STATUS_TASK;
            current_task.setTarget(target);
        } else if (current_task == null && target != null) {
            // time for new task!
            boolean friend = target.getClass() == this.getClass();
            if (healer && friend) {
                takeTaskHeal();
            } else {
                takeTaskHit();
            }
            if (current_task != null)// needed for reference in task
                current_task.setTarget(target);
        } else if (current_task != null) {
            status = STATUS_TASK;
            current_task.setTarget(target);
        }
    }

    public boolean isDead() {
        return stats.getHp() <= 0;
    }

    @Override
    public boolean canBeDeleted() {
        return (currentDeadAlpha <= 0);//is after dead animation
    }

    public boolean canMove() {
        return status != STATUS_DEAD && status != STATUS_TASK && status != STATUS_STUN;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Pointer getPointer() {
        return pointer;
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }

    public Sprite getTarget() {
        return target;
    }

    /**
     * set target for attack/heal
     *
     * @param target
     */
    public void setTarget(Sprite target) {
        //change target durning loading task will cancel current task
        if (target != this.target && current_task != null) {
            cancelCurrentTask();
        }

        if (!isNewPointNeeded()) {
            this.target = target;
            if (pointer != null)
                pointer.setTarget(target);
        }

        if (this.target != null) {
            lookAtTarget(target);
            if (pointer != null)
                pointer.show();

            takeNextTask();//if have any in current or todo list
        } else {
            cancelCurrentTask();
            if (pointer != null)
                pointer.hide();
        }
    }

    private boolean isNewPointNeeded() {
        return !canMove() && current_task != null && current_task.getCurrent_duration() < current_task.getDirection() && current_task.getCurrent_duration() > 0;
    }

    /**
     * change directon of animation - look at target
     *
     * @param target
     */
    private void lookAtTarget(Sprite target) {
        if (target != null)
            lookLeft = (target.x < x);

    }

    /**
     * take default heal task
     */
    private void takeTaskHeal() {
        if (canTakeDefaultTask()) {
            current_task = task_heal;
            status = STATUS_TASK;

            if (task_heal == null) {
                Log.d(tag, "NOPE my heal task is NULL!");
            } else
                Log.d(tag, "OK! heal");
        }
    }

    /**
     * take default hit task
     */
    private void takeTaskHit() {
        if (canTakeDefaultTask()) {
            current_task = task_hit;
            status = STATUS_TASK;

            if (task_hit == null) {
                Log.d(tag, "NOPE my hit task is NULL!");
            } else
                Log.d(tag, "OK! hit");
        }
    }

    /**
     * if i don't have current task and have empty list of task i can take a new one!
     */
    private boolean canTakeDefaultTask() {
        return (current_task == null && tasks.size() == 0);
    }

    /**
     * sets a color filter on character
     */
    public void setPaintFilter(int color) {
        LightingColorFilter darkFilter = new LightingColorFilter(color, 0);
        paint.setColorFilter(darkFilter);
    }

    /**
     * range of sprite
     */
    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    /**
     * get animation direction
     */
    public boolean isLookLeft() {
        return lookLeft;
    }

    /**
     * set animation direction
     */
    public void setLookLeft(boolean lookLeft) {
        this.lookLeft = lookLeft;
    }

    public boolean isHealer() {
        return healer;
    }

    public void setHealer(boolean healer) {
        this.healer = healer;
    }

    public Task getTask_hit() {
        return task_hit;
    }

    public void setTask_hit(Task task_hit) {
        this.task_hit = task_hit;
    }

    public Task getTask_heal() {
        return task_heal;
    }

    public void setTask_heal(Task task_heal) {
        this.task_heal = task_heal;
    }

    public float getStandardSpeed() {
        return standardSpeed;
    }

    /**
     * standard speed will be like proc*constV
     */
    public void setStandardSpeed(float proc) {
        standardSpeed = Toolbox.screenHeight / 6;
        standardSpeed *= proc;
    }

    /**
     * IMPORTANT! REMOVE ALL REFERENCES FOR DELETED OBJECT!
     */
    public void spriteIsDead(Sprite sprite) {
        if (target == sprite) {
            target = null;
            if (pointer != null)
                pointer.hide();
        }
    }

    /**
     * turn on animation of dying
     */
    public void deadAnimation() {
        //abbadon all my tasks
        if (current_task != null) {
            current_task.cancelTask();
            current_task = null;
        }
        tasks.clear();

        //animation of fade out in alpha
        status = STATUS_DEAD;
    }

    /**
     * recylce bitmaps and removes references
     */
    @Override
    public void freeMemory() {
        if (task_heal != null)
            task_heal.freeMemory();

        if (task_hit != null)
            task_hit.freeMemory();

        Toolbox.freeMemory(bmpIdStay, false);
        stayL = null;
        Toolbox.freeMemory(bmpIdStay, true);
        stayR = null;
        Toolbox.freeMemory(bmpIdWalk, false);
        walkL = null;
        Toolbox.freeMemory(bmpIdWalk, true);
        walkR = null;
        if (pointer != null)
            pointer.freeMemory();

    }

    /**
     * get string about Sprite' stats
     *
     * @return
     */
    public String statsInfo() {
        return "hp = " + stats.getHpStart() + " \tstr = " + stats.getStr() + " \tdef = " + stats.getDef();

    }

    /**
     * prepare Path above Sprite with star. Remember to update value starRotation in update() - star will rotate
     */
    private void prepareShockStar() {
        int NUMBER_OF_ARMS = 5;
        float outRadius = Math.max(width / 8, height / 8);
        float inRadius = outRadius / 2;
        float angle = (float) (Math.PI / NUMBER_OF_ARMS);
        Point center = new Point((int) x, (int) (y - starOffsetY));
        PointF point = new PointF();

        shockStar.reset();
        for (int i = 0; i < 2 * NUMBER_OF_ARMS; i++) {
            // get ones out circle and next time inner circle
            double r = (i & 1) == 0 ? outRadius : inRadius;
            point.set((float) (center.x + Math.cos(i * angle + starRotation) * r), (float) (center.y + Math.sin(i * angle + starRotation) * r));
            if (i == 0)
                shockStar.moveTo(point.x, point.y);
            else
                shockStar.lineTo(point.x, point.y);
        }
        shockStar.close();
    }

    /**
     * kill this sprite instantly
     */
    public void suicide() {
        setHp(0);
        deadAnimation();

        GameView.game.get().spriteIsDead(this);

    }

    public void setHp(float hp) {
        this.stats.setHp(hp);
        lifeBar.setHP(hp);
    }

    public float getHpStart() {
        return stats.getHpStart();
    }

    public Stats getStats() {
        return stats;
    }
}
