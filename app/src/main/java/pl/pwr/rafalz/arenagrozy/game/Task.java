package pl.pwr.rafalz.arenagrozy.game;

import java.util.List;

import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.game.effects.FillScreen;
import pl.pwr.rafalz.arenagrozy.game.effects.Missle;
import pl.pwr.rafalz.arenagrozy.game.effects.Wave;
import pl.pwr.rafalz.arenagrozy.game.sprites.Hero;
import pl.pwr.rafalz.arenagrozy.game.sprites.Sprite;
import pl.pwr.rafalz.arenagrozy.game.ui.Text;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import pl.pwr.rafalz.arenagrozy.view.GameView;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;


/**
 * This class represents all kinds of attacks/heal. Keeps information about: animation, special effects, power, range mode, duration etc. Remember to use update() for refreshing animation and checking state of special effects. Range mode ids are in class Sprite.
 *
 * @author Zientara
 */
public class Task implements OnCompletionListener {
    private final String tag = getClass().getName();

    public static final int ANIM_ONE_DIRECTION = 0;
    public static final int ANIM_TWO_DIRECTIONS = 1;

    private Bitmap[] animL;
    private Bitmap[] animR;
    private int bmpId;

    private int taskId; //konkretna nazwa skilla
    private int current_frame;
    private int direction = -1; //-1 oznacza ze domyslnie nie wspiera kierunku animacji
    private float loading; //czas regeneracji ataku przy ikonkach, przy WROGACH = 0!!

    //private Task task;//zadanie rekurencyjne. jesli skonczylem siebie podaje paleczke nizej :D

    //old needed info about task
    private int range_mode;//dystansowy, w zwarciu, obszarowy, globalny
    private float duration;
    private float current_duration;//ile mineloo juz z ladowania?

    private boolean need_target;
    private int frames;
    private Sprite parent;
    private Sprite target;


    //NEW INFORMATIONS AND IDEAS
    private boolean healingSkill;
    private float hitTime;//when
    private boolean hitCompleted;
    private float hitR; //distance in RANGE LOCAL

    //for missle
    private boolean missleMode;
    private float missleTime;
    private boolean missleCompleted;
    private int missleColor;
    private float missleScale;

    //for wave - from caster - only visualization!
    private boolean wave_mode;
    private boolean waveCompleted;
    private boolean waveOnTarget = true;//wave should appear on my target or on myself
    private float waveTime; //when wave should appear
    private float waveR;//R of wave
    private int waveColor;
    private float waveDR;//speed of changing R
    private int waveDA;//speed of changing alpha
    //for whole screen - only visualization!
    private boolean fillCompleted;
    private boolean fill_mode;
    private float fillTime;
    private int fillColor;
    private int fillAlpha;
    private int fillDA;
    private float shockTime;

    private int textColor;

    private float runSpeed; //for range clos - fe charge!

    private float power;

    private float stunTime;
    private boolean allahAkbarMode;

    private MediaPlayer mediaPlayer;
    private int soundId = -1;//-1 - dont play music
    private float soundTime;
    private boolean soundCompleted;


    public Task(int taskId, int range_mode,
                int directions, int bmpId, int frames, Sprite parent) {
        super();
        this.bmpId = bmpId;
        this.parent = parent;
        this.taskId = taskId;
        this.range_mode = range_mode;

        //default values
        this.need_target = (range_mode == Sprite.RANGE_CLOSE || range_mode == Sprite.RANGE_FAR);
        waveR = parent.getWidth() * 2;
        textColor = Color.WHITE;
        this.power = 1;
        this.duration = 0.5f;
        loading = 4f;

        if (range_mode == Sprite.RANGE_LOCAL)
            waveOnTarget = false;

        animL = Toolbox.getFramesFromBitmap(bmpId, frames);

        if (directions == ANIM_TWO_DIRECTIONS) {
            direction = 0;
            animR = Toolbox.getMirroredFrames(bmpId);
        } else
            direction = -1;

        this.frames = frames;
        current_duration = 0;
        current_frame = 0;
    }


    /**
     * checks and update every element of Task: special effects, animation,
     * loading, hit/heal etc. Must be called when Sprite is using this Task.
     *
     * @param dt
     */
    public float update(float dt) {
        current_frame = (int) ((current_duration * frames) / duration);

        float wantedDT = duration - current_duration;
        if (wantedDT >= dt) {
            current_duration += dt;
            checkExtra();
            return 0;
        } else {//finish and still have some time
            current_duration = duration;
            checkExtra();
            return dt - wantedDT;
        }
    }

    /**
     * check if there is any special action to do
     */
    private void checkExtra() {
        checkSound();
        checkWave();
        checkFill();
        checkHit();
        checkMissle();
    }

    /**
     * checks if task is ready to use wave, and if yes - it will be used
     */
    private void checkWave() {
        if (wave_mode && current_duration >= waveTime && !waveCompleted) {
            waveCompleted = true;
            Wave w = new Wave(waveR);
            if (waveOnTarget)
                w.setPosition(parent.getTarget());
            else
                w.setPosition(parent);

            if (waveColor != 0)
                w.setColor(waveColor);
            if (waveDA != 0)
                w.setAlphaDelta(waveDA);
            if (waveDR != 0)
                w.setSizeDelta(waveDR);

            w.update(current_duration - waveTime);//update if dt was greater
            GameView.game.get().addWave(w);
        }
    }

    /**
     * check and uses missle if setted
     */
    private void checkMissle() {
        if (missleMode && range_mode == Sprite.RANGE_FAR) {
            if (current_duration >= missleTime && !missleCompleted) {
                missleCompleted = true;

                Missle m = new Missle(parent.x, parent.y, parent.getR() / 2,
                        parent.getTarget());
                m.setColor(waveColor);
                m.setyModifier(parent.getHeight() / 2);

                if (missleColor != 0)
                    m.setColor(missleColor);
                if (missleScale != 0)
                    m.setscale(missleScale);


                m.update(current_duration - missleTime);// update if dt was
                // greater
                GameView.game.get().addMissle(m);
            }
        }
    }

    /**
     * check and uses sound
     */
    private void checkSound() {
        if (soundId != -1 && current_duration >= soundTime && !soundCompleted) {
            soundCompleted = true;


            if (GameView.game.get().playSounds) {
                mediaPlayer = MediaPlayer.create(GameView.game.get().getContext(), soundId);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.start();
            }

            //GameView.game.get().addWave(w);
        }
    }

    /**
     * check and uses fill effect if setted
     */
    private void checkFill() {
        if (fill_mode && current_duration >= fillTime && !fillCompleted) {
            fillCompleted = true;
            FillScreen f = new FillScreen();
            if (fillColor != 0)
                f.setColor(fillColor);

            if (fillAlpha != 0)
                f.setStartAlpha(fillAlpha);
            if (fillDA != 0)
                f.setDeltaAlpha(fillDA);

            f.update(current_duration - fillTime);//update if dt was greater
            GameView.game.get().addFillScreen(f);
        }
    }

    /**
     * check and hits/heal
     */
    private void checkHit() {
        if (current_duration >= hitTime && !hitCompleted) {
            Log.d(tag, "HIT!");
            hitCompleted = true;

            switch (range_mode) {
                case Sprite.RANGE_FAR:
                case Sprite.RANGE_CLOSE:
                    //check target
                    if (target != null)
                        hitCloseFar();
                    else
                        cancelTask();
                    break;

                case Sprite.RANGE_LOCAL:
                    hitLocal();
                    break;

                case Sprite.RANGE_GLOBAL:
                    hitGlobal();
                    break;

            }


            if (allahAkbarMode) {
                parent.suicide();

            }
        }


    }

    /**
     * prepare list of enemies in range and hit/heal them/him. Hits/heal one character, or
     * more if hitR is setted. Then Sprite will hit/heal all enemies in range
     */
    private void hitCloseFar() {
        // target to targer
        if (hitR == 0)
            hitSprite(parent.getTarget());
        else {
            boolean isHero = (parent instanceof Hero);
            // hurt enemies in R
            Sprite target = parent.getTarget();
            List<Sprite> targets = null;

            if (waveOnTarget) {// from target position
                if ((healingSkill && !isHero) || (!healingSkill && isHero))
                    targets = GameView.game.get().getEnemiesInRange(
                            target.getX(), target.getY(), hitR);
                else
                    targets = GameView.game.get().getHeroesInRange(
                            target.getX(), target.getY(), hitR);
            } else {// from my position
                if ((healingSkill && !isHero) || (!healingSkill && isHero))
                    targets = GameView.game.get().getEnemiesInRange(
                            parent.getX(), parent.getY(), hitR);
                else
                    targets = GameView.game.get().getHeroesInRange(
                            parent.getX(), parent.getY(), hitR);

            }
            hitSprites(targets);
        }
    }


    /**
     * hit/heal all enemies/ally in wanted hitR
     */
    private void hitLocal() {
        boolean isHero = (parent instanceof Hero);
        List<Sprite> targets = null;
        if ((healingSkill && !isHero) || (!healingSkill && isHero))
            targets = GameView.game.get().getEnemiesInRange(parent.getX(), parent.getY(), hitR);
        else
            targets = GameView.game.get().getHeroesInRange(parent.getX(), parent.getY(), hitR);
        hitSprites(targets);

    }

    /**
     * hit/heal enemies/ally on whole screen
     */
    private void hitGlobal() {
        boolean isHero = (parent instanceof Hero);
        List<GameObject> targets = null;
        if ((healingSkill && !isHero) || (!healingSkill && isHero))
            targets = GameView.game.get().getEnemies();
        else
            targets = GameView.game.get().getHeroes();
        hitSpritesGO(targets);
    }

    private void hitSpritesGO(List<GameObject> targets) {
        for (GameObject s : targets)
            hitSprite((Sprite) s);
    }

    private void hitSprites(List<Sprite> targets) {
        for (Sprite s : targets)
            hitSprite(s);
    }

    private void hitSprite(Sprite s) {
        if (s != null) {
            float dmg = 0;

            if (healingSkill)
                dmg = s.heal(this);//healing!
            else
                dmg = s.hit(this, stunTime);

            //effects
            Text t = null;
            if (dmg > 1)
                t = new Text(dmg);
            else {
                t = new Text(GameView.game.get().getStringR(R.string.game_miss));
                if (GameView.game.get().playSounds) {
                    mediaPlayer = MediaPlayer.create(GameView.game.get()
                            .getContext(), R.raw.whoosh1);
                    mediaPlayer.setOnCompletionListener(this);
                    mediaPlayer.start();
                }

            }
            t.setPosition(s);
            t.setColor(textColor);
            t.update(current_duration - hitTime);//update if dt was greater
            GameView.game.get().addText(t);


        } else if (Toolbox.TEST_MODE)
            Log.d(tag, "hitSprite s== NULL!");
    }

    public Bitmap getCurrentBmp() {
        if (isDualDirection()) {
            if (parent.isLookLeft())
                return animL[current_frame];
            else
                return animR[current_frame];
        } else
            return animL[current_frame];


    }

    /**
     * is task finished?
     *
     * @return
     */
    public boolean isCompleted() {
        return (current_duration >= duration);
    }


    public void cancelTask() {
        current_duration = 0;
        current_frame = 0;
        waveCompleted = false;
        hitCompleted = false;
        fillCompleted = false;
        soundCompleted = false;
        missleCompleted = false;
        target = null;
    }

    public Sprite getTarget() {
        return target;
    }

    public void setTarget(Sprite target) {
        this.target = target;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getRange_mode() {
        return range_mode;
    }

    public void setRange_mode(int range_mode) {
        this.range_mode = range_mode;
    }


    public float getDuration() {
        return duration;
    }


    /**
     * also sets hit Time and sound time
     *
     * @param f
     */
    public void setDuration(float f) {
        soundTime = hitTime = f;
        this.duration = f;
    }


    public float getCurrent_duration() {
        return current_duration;
    }


    public void setCurrent_duration(int current_duration) {
        this.current_duration = current_duration;
    }


    public float getPower() {
        return power;
    }


    public void setPower(float power) {
        this.power = power;
    }


    public boolean isNeed_target() {
        return need_target;
    }


    public void setNeed_target(boolean need_target) {
        this.need_target = need_target;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }


    public int getCurrent_frame() {
        return current_frame;
    }


    public void setCurrent_frame(int current_frame) {
        this.current_frame = current_frame;
    }


    public int getDirection() {
        return direction;
    }

    /**
     * czy animacja jest w 2 strony?
     *
     * @return
     */
    public boolean isDualDirection() {
        if (direction == -1)
            return false;
        else
            return true;
    }

    /**
     * get time needed for spell button (UI)
     *
     * @return
     */
    public float getLoading() {
        return loading;
    }

    public void setLoading(float time) {
        this.loading = time;
    }

    public float getHitTime() {
        return hitTime;
    }


    public void setHitTime(float hitTime) {
        this.hitTime = hitTime;
    }


    public int getSoundId() {
        return soundId;
    }


    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }


    public boolean isWave_mode() {
        return wave_mode;
    }


    public void setWave_mode(boolean wave_mode) {
        this.wave_mode = wave_mode;
        waveTime = duration;
    }


    public float getWaveTime() {
        return waveTime;
    }


    public void setWaveTime(float waveTime) {
        this.waveTime = waveTime;
    }


    public float getWaveR() {
        return waveR;
    }


    public void setWaveR(float waveR) {
        this.waveR = waveR;
    }


    public int getWaveColor() {
        return waveColor;
    }


    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
    }


    public float getWaveDR() {
        return waveDR;
    }

    /*
     * value in %
     */
    public void setWaveDR(float waveDR) {
        this.waveDR = waveDR;
    }


    public float getWaveDA() {
        return waveDA;
    }


    public void setWaveDA(int waveDA) {
        this.waveDA = waveDA;
    }


    public boolean isFill_mode() {
        return fill_mode;
    }


    public void setFill_mode(boolean fill_mode) {
        this.fill_mode = fill_mode;
        fillTime = duration;
    }


    public float getFillColorTime() {
        return fillTime;
    }


    public void setFillColorTime(float fillColorTime) {
        this.fillTime = fillColorTime;
    }


    public int getFillColor() {
        return fillColor;
    }


    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }


    public float getShockTime() {
        return shockTime;
    }


    public void setShockTime(float shockTime) {
        this.shockTime = shockTime;
    }


    public float getRunSpeed() {
        return runSpeed;
    }


    public void setRunSpeed(float runSpeed) {
        this.runSpeed = runSpeed;
    }


    public Sprite getParent() {
        return parent;
    }


    public void setParent(Sprite parent) {
        this.parent = parent;
    }


    public boolean isWaveOnTarget() {
        return waveOnTarget;
    }


    public void setWaveOnTarget(boolean waveOnTarget) {
        this.waveOnTarget = waveOnTarget;
    }


    public float getFillTime() {
        return fillTime;
    }


    public void setFillTime(float fillTime) {
        this.fillTime = fillTime;
    }


    public int getFillAlpha() {
        return fillAlpha;
    }


    public void setFillAlpha(int fillAlpha) {
        this.fillAlpha = fillAlpha;
    }


    public int getFillDA() {
        return fillDA;
    }


    public void setFillDA(int fillDA) {
        this.fillDA = fillDA;
    }


    public boolean isHealingSkill() {
        return healingSkill;
    }

    /**
     * also sets color of text
     *
     * @param healingSkill
     */
    public void setHealingSkill(boolean healingSkill) {
        this.healingSkill = healingSkill;
        if (healingSkill)
            textColor = Color.GREEN;
    }

    public float getHitR() {
        return hitR;
    }

    public void setHitR(float hitR) {
        this.hitR = hitR;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public float getStunTime() {
        return stunTime;
    }

    public void setStunTime(float stunTime) {
        this.stunTime = stunTime;
    }

    public void setSuicideMode(boolean allahAkbarMode) {
        this.allahAkbarMode = allahAkbarMode;
    }


    public float getSoundTime() {
        return soundTime;
    }


    public void setSoundTime(float soundTime) {
        this.soundTime = soundTime;
    }

    public boolean isMissleMode() {
        return missleMode;
    }


    /**
     * also sets time for missle. hit - Missle.flightTime
     *
     * @param missleMode
     */
    public void setMissleMode(boolean missleMode) {
        this.missleMode = missleMode;
        missleTime = hitTime - Missle.flightTime;
    }


    public int getMissleColor() {
        return missleColor;
    }


    public void setMissleColor(int missleColor) {
        this.missleColor = missleColor;
    }


    public float getMissleScale() {
        return missleScale;
    }


    public void setMissleScale(float missleScale) {
        this.missleScale = missleScale;
    }


    /**
     * recycle bitmaps and delete references
     */
    public void freeMemory() {
        parent = null;
        target = null;
        //animL
        Toolbox.freeMemory(bmpId, false);
        animL = null;

        if (isDualDirection()) {
            //animR
            Toolbox.freeMemory(bmpId, true);
            animR = null;
        }

        mediaPlayer = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp.isPlaying())
            mp.stop();
        mp.release();
    }


}
