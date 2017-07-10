package pl.pwr.rafalz.arenagrozy.view;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pl.pwr.rafalz.arenagrozy.GameActivity;
import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.data.DataBase;
import pl.pwr.rafalz.arenagrozy.data.EnemyDB;
import pl.pwr.rafalz.arenagrozy.data.HeroDB;
import pl.pwr.rafalz.arenagrozy.data.LevelDB;
import pl.pwr.rafalz.arenagrozy.data.LevelFrame;
import pl.pwr.rafalz.arenagrozy.data.TaskDB;
import pl.pwr.rafalz.arenagrozy.data.TextDB;
import pl.pwr.rafalz.arenagrozy.game.Enemy;
import pl.pwr.rafalz.arenagrozy.game.FillScreen;
import pl.pwr.rafalz.arenagrozy.game.GameLogic;
import pl.pwr.rafalz.arenagrozy.game.GameObject;
import pl.pwr.rafalz.arenagrozy.game.Hero;
import pl.pwr.rafalz.arenagrozy.game.LifeBar;
import pl.pwr.rafalz.arenagrozy.game.Missle;
import pl.pwr.rafalz.arenagrozy.game.Pivot;
import pl.pwr.rafalz.arenagrozy.game.Sprite;
import pl.pwr.rafalz.arenagrozy.game.Text;
import pl.pwr.rafalz.arenagrozy.game.Wave;
import pl.pwr.rafalz.arenagrozy.gameUI.ButtonV;
import pl.pwr.rafalz.arenagrozy.gameUI.SpellButton;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import pl.pwr.rafalz.arenagrozy.xmlparser.XMLData;
import pl.pwr.rafalz.arenagrozy.xmlparser.XMLEnemy;
import pl.pwr.rafalz.arenagrozy.xmlparser.XMLHint;
import pl.pwr.rafalz.arenagrozy.xmlparser.XMLLevelInfo;
import pl.pwr.rafalz.arenagrozy.xmlparser.XMLText;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Main class for handling all game
 *
 * @author Zientara
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final boolean LOW_MEMORY_TEST = Toolbox.TEST_MODE;

    private float fps = 0;

    private final String tag = getClass().getName();
    private boolean showHints = true;//change if u want
    private MediaPlayer mediaPlayer;
    public boolean playMusic;
    public boolean playSounds;

    private GameLogic gameLogic;

    private int gainedExp = 0;
    private boolean memoryFree;
    private float scaleLowMemory = 1f;


    //ending
    boolean[] getExp = new boolean[3];
    int aliveHeroes = 3;
    int expPerHero;

    //ending animation
    private float endTime = 0;
    private boolean endFirst;
    private float endTimeFirst = 2f; // time to get exp
    private boolean endSecond;
    private float endTimeSecond = 2f; // time to get exp
    private boolean endThird;
    private float endTimeThird = 1f; //show dialog

    //level info
    private XMLLevelInfo levelInfo;
    private int level;

    private boolean finishedLevel;
    private boolean readingSection; //is true when asyncTask is working
    private XMLHint hintData;
    //private XMLText textData;
    private String textData = null;
    private int textColor;
    private List<Sprite> sectionSprites;


    private static int buttonSize;
    private static int touchError;
    private long touchTime;
    private final long touchTimeTap = 200;
    public static WeakReference<GameView> game;


    private List<GameObject> sprites = new ArrayList<GameObject>();
    private List<GameObject> ground = new ArrayList<GameObject>();

    private List<GameObject> heroes = new ArrayList<GameObject>();
    private List<GameObject> enemies = new ArrayList<GameObject>();

    private List<GameObject> firstPlan = new ArrayList<GameObject>();

    private List<Pivot> pivots = new ArrayList<Pivot>();

    //handle touch
    private Pivot touchPivot;

    private Rect playground;
    private Rect dst;
    private Bitmap backroundBmp;
    private int bgStartWid;
    private int bgStartHei;
    private Paint paint;
    private int pauseBgCol;


    //UI
    private List<ButtonV> switch_hero = new ArrayList<ButtonV>();
    private List<List<SpellButton>> spells;
    private int choose_mover = 0;

    //pause state
    private ButtonV pauseButton;
    private ButtonV musicButton;
    private ButtonV soundButton;
    private Text musicText;
    private Text soundText;
    private boolean pause;
    private boolean canControl = true; //forbit controling heroes - end of level
    private boolean stateLose;


    public GameView(Context context) {
        super(context);
        GameView.game = new WeakReference<GameView>(this);
        //GameView.context = getContext();

        Toolbox.initScreenInfo(getContext());

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(Toolbox.screenHeight / 20);

        sectionSprites = new ArrayList<Sprite>();

        buttonSize = this.getResources().getDimensionPixelOffset(R.dimen.button_medium);
        touchError = this.getResources().getDimensionPixelOffset(R.dimen.touch_error);
        pauseBgCol = this.getResources().getColor(R.color.pause_background);
        playMusic = DataBase.getOptMusic();
        playSounds = DataBase.getOptSound();

        if (touchError < Toolbox.gettouchErrorMax())
            touchError = Toolbox.gettouchErrorMax();

        if (buttonSize > Toolbox.screenWidth / 8)
            buttonSize = Toolbox.screenWidth / 8;

        touchPivot = new Pivot(0, 0, touchError);
        touchPivot.setVisible(false);

        setFocusable(true);
        getHolder().addCallback(this);
        if (Toolbox.TEST_MODE)
            Log.d("GameView", "Koniec konstruktora GameView");
    }


    public void draw(Canvas c) {
        if (!memoryFree) {
            c.drawBitmap(backroundBmp, null, dst, paint);

            // pivots at the bottom
            for (Pivot p : pivots)
                p.draw(c);

            touchPivot.draw(c);

            for (int i = 0; i < ground.size(); i++)
                ground.get(i).draw(c);

            for (GameObject o : sprites)
                o.draw(c);

            for (GameObject o : firstPlan)
                o.draw(c);

            // draw UI
            for (ButtonV b : switch_hero)
                b.draw(c);

            List<SpellButton> spell_list = spells.get(choose_mover);
            for (SpellButton s : spell_list)
                s.draw(c);

            if (pause) {
                c.drawColor(pauseBgCol);
                musicButton.draw(c);
                musicText.draw(c);

                soundButton.draw(c);
                soundText.draw(c);
            }

            pauseButton.draw(c);

        } else {//memory is empty
            c.drawColor(Color.BLACK);
        }


        if (Toolbox.TEST_MODE) {
            int y = Toolbox.screenHeight - Toolbox.screenHeight / 40;
            c.drawText((int) fps + " FPS", 0, y, paint);
        }
    }


    public GameView update(float dt) {
        if (!pause && canControl)
            updateRunning(dt);
        else if (pause && canControl)
            updatePause(dt);
        else
            updateEnd(dt);


        return this;
    }

    private void updateEnd(float dt) {
        updateRunning(dt);
    }

    private void updateRunning(float dt) {
        for (int i = 0; i < sprites.size(); i++) {
            sprites.get(i).update(dt);
        }

        //cleaning up list of enemies if any Enemy is dead
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Sprite s = (Sprite) enemies.get(i);
            if (s.canBeDeleted())
                removeEnemy(s);
        }

        //read new wave
        if (!readingSection && !sectionSprites.isEmpty()) {
            for (Sprite s : sectionSprites)
                addEnemy((Enemy) s);

            sectionSprites.clear();

            if (LOW_MEMORY_TEST)
                onLowMemory();
        }

        //hint
        if (hintData != null) {
            final int strId = TextDB.getHintText(hintData.getHintId());
            hintData = null;

            getHandler().post(new Runnable() {
                public void run() {
                    GameActivity.act.get().showHintDialog(strId);

                }
            });
        }

        if (!finishedLevel && enemies.isEmpty() && !readingSection) {//out of enemies?
            readingSection = true;
            getHandler().post(new Runnable() {
                public void run() {
                    new ReadSectionAsyncTask().execute();
                }
            });
        }

        checkFinish(dt);

        // game UI -buttons on top!
        for (ButtonV b : switch_hero)
            b.update(dt);

        pauseButton.update(dt);
        musicButton.update(dt);
        soundButton.update(dt);

        for (int i = 0; i < spells.size(); i++) {
            List<SpellButton> spell_list = spells.get(i);
            for (int j = 0; j < spell_list.size(); j++)
                spell_list.get(j).update(dt);
        }

        for (int i = ground.size() - 1; i > -1; i--) {
            GameObject go = ground.get(i);
            go.update(dt);
            if (go.canBeDeleted())
                ground.remove(i);
        }

        for (int i = firstPlan.size() - 1; i > -1; i--) {
            GameObject go = firstPlan.get(i);
            go.update(dt);
            if (go.canBeDeleted())
                firstPlan.remove(i);
        }

        for (Pivot p : pivots)
            p.update(dt);

        touchPivot.update(dt);


        //sorting layers
        Toolbox.bubblesort(sprites);
    }

    /**
     * check if end animation must be prepared
     *
     * @param dt
     */
    private void checkFinish(float dt) {
        if (finishedLevel) {
            endTime += dt;
            if (endTimeFirst <= endTime && !endFirst) {
                if (Toolbox.TEST_MODE)
                    Log.d(tag, "END 1 animation");
                endTime -= endTimeFirst;
                endFirst = true;
                if (aliveHeroes > 0)
                    saveProgress();//win
                else
                    lose();

            }
            //this animation is only for winner
            if (!stateLose) {
                if (endTimeSecond <= endTime && !endSecond && endFirst) {
                    if (Toolbox.TEST_MODE)
                        Log.d(tag, "END 2 animation");
                    endTime -= endTimeSecond;
                    endSecond = true;
                    endAnimationSecond();
                }

                if (endTimeThird <= endTime && !endThird && endFirst
                        && endSecond) {
                    if (Toolbox.TEST_MODE)
                        Log.d(tag, "END 3 animation");
                    endTime -= endTimeThird;
                    endThird = true;
                    endAnimationThird();
                }
            }

        }

    }

    private void updatePause(float dt) {
        //only buttons in pause menu
        pauseButton.update(dt);
        musicButton.update(dt);
        soundButton.update(dt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (!pause && canControl && aliveHeroes > 0)
            return onTouchRunning(x, y, event);
        else if (pause && canControl)
            return onTouchPause(x, y, event);
        else
            return onTouchEnd(x, y, event);
    }

    public boolean onTouchRunning(int x, int y, MotionEvent event) {
        Hero hero = (Hero) heroes.get(choose_mover);
        boolean isHealer = hero.isHealer();

        //if hero is a healer get nearest sprite - also another hero but not me!
        Sprite the_nearest_sprite = (isHealer ? getNearestSpriteExceptFor(x, y + Toolbox.gettouchCor(), hero)
                : getNearestEnemy(x, y + Toolbox.gettouchCor()));

        boolean tapHero = false;
        if (System.currentTimeMillis() < touchTime && hero.distance(x, y) < touchError)
            tapHero = true;


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchTime = System.currentTimeMillis() + touchTimeTap;
                if (isOnPlayground(x, y)) {
                    touchPivot.show();
                    touchPivot.setColor(hero.getPivot().getColor());

                    if (the_nearest_sprite != null && the_nearest_sprite.distance(x, y + Toolbox.gettouchCor()) < touchError) {
                        touchPivot.setPosition(the_nearest_sprite);
                    } else {
                        touchPivot.setPosition(x, y);
                    }

                }

                Hero clickedHero = getNearestHero(x, y + Toolbox.gettouchCor());
                if (clickedHero.distance(x, y + Toolbox.gettouchCor()) > touchError) {
                    clickedHero = null; //no hero is clicked
                } else {
                    choose_mover = clickedHero.getWhichHero() - 1;
                    switch_hero.get(choose_mover).forceClick();
                    touchPivot.setColor(((Sprite) heroes.get(choose_mover)).getPivot().getColor());
                }
                if (pauseButton.isClicked(x, y)) {
                    pause();
                }

                //choose hero
                for (int i = 0; i < switch_hero.size(); i++) {
                    ButtonV butt = switch_hero.get(i);
                    Hero he = (Hero) heroes.get(i);
                    if (butt.isClicked(x, y) && !he.isDead()) {
                        choose_mover = i;
                        butt.focus();
                        //load touch pointer settings
                        addWaveUnderCurrentHero();
                    } else {
                        butt.unfocus();
                    }
                }
                switch_hero.get(choose_mover).focus();

                //choose spell
                List<SpellButton> spell_list = spells.get(choose_mover);
                for (int i = 0; i < spell_list.size(); i++) {
                    SpellButton s = spell_list.get(i);
                    if (s.isClicked(x, y))
                        if (s.useSpell())
                            hero.useSpell(s.getSpell());
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isOnPlayground(x, y) && !tapHero && !hero.isStunned()) {
                    if (the_nearest_sprite != null && the_nearest_sprite.distance(x, y + Toolbox.gettouchCor()) < touchError) {
                        hero.setTarget(the_nearest_sprite);
                        setHasTarger(choose_mover, true);

                        touchPivot.hide();
                    } else {
                        hero.setTarget(null); //canceling current task
                        hero.getPivot().move(x, y);
                        hero.getPivot().show();
                        hero.changeDestination(x, y, 0, false);
                        setHasTarger(choose_mover, false);
                        touchPivot.hide();
                    }
                } else {
                    touchPivot.hide();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                //refresh pointer position if finger is on playground
                if (isOnPlayground(x, y)) {
                    //near target
                    if (the_nearest_sprite != null && the_nearest_sprite.distance(x, y + Toolbox.gettouchCor()) < touchError)//
                    {
                        touchPivot.move((int) the_nearest_sprite.getX(), (int) the_nearest_sprite.getY());
                        touchPivot.setVisible(true);
                    } else {
                        //on ground
                        touchPivot.move(x, y);
                        touchPivot.setVisible(true);
                    }
                } else {
                    touchPivot.setVisible(false);
                }

                break;
        }
        return true;

    }

    /**
     * special effect. Show choosen hero
     */
    private void addWaveUnderCurrentHero() {
        Hero h = (Hero) heroes.get(choose_mover);
        Wave w = new Wave(h.getWidth() / 2);
        w.setPosition(h);
        w.setAlphaDelta(60);
        w.setColor(h.getPivot().getColor());
        addWave(w);
    }


    public boolean onTouchPause(int x, int y, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (pauseButton.isClicked(x, y)) {
                    resume();
                }

                if (musicButton.isClicked(x, y)) {
                    playMusic = !playMusic;
                    DataBase.setOptMusic(playMusic);

                    //refresh stat
                    if (playMusic) {
                        mediaPlayer.start();

                    } else {
                        mediaPlayer.pause();
                    }
                    musicButton.setIs_on(playMusic);
                    musicText.setText(getMusicStateString());
                }

                if (soundButton.isClicked(x, y)) {
                    playSounds = !playSounds;
                    DataBase.setOptSound(playSounds);

                    soundButton.setIs_on(playSounds);
                    soundText.setText(getSoundsStateString());
                }

                break;
        }
        return true;
    }

    public boolean onTouchEnd(int x, int y, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //nth
                break;
        }
        return true;
    }

    /**
     * refresh state of button if spell needs a target
     *
     * @param whichHero
     * @param haveTarget
     */
    public void setHasTarger(int whichHero, boolean haveTarget) {
        List<SpellButton> ms = spells.get(whichHero);
        for (SpellButton sb : ms) {
            sb.setHave_target(haveTarget);
        }
    }


    /*
     * 				Methods for getting wanted sprites
     */
    public Hero getNearestHero(float x, float y) {
        return (Hero) getNearestSprite(heroes, x, y);
    }

    public Enemy getNearestEnemy(float x, float y) {
        return (Enemy) getNearestSprite(enemies, x, y);
    }

    public Sprite getNearestSprite(float x, float y) {
        return getNearestSprite(sprites, x, y);
    }

    public Sprite getNearestSpriteExceptFor(float x, float y, Sprite s) {
        return getNearestSpriteExceptFor(sprites, x, y, s);
    }

    private Sprite getNearestSprite(List<GameObject> list, float x, float y) {
        Sprite s = null;
        float distance = Toolbox.screenWidth;
        for (int i = 0; i < list.size(); i++) {
            GameObject e = list.get(i);
            if (!e.isDead() && distance > e.distance(x, y)) {
                distance = (float) e.distance(x, y);
                s = (Sprite) list.get(i);
            }
        }
        return s;
    }

    private Sprite getNearestSpriteExceptFor(List<GameObject> list, float x, float y, GameObject exceptFor) {
        Sprite s = null;
        float distance = Toolbox.screenWidth;
        for (int i = 0; i < list.size(); i++) {
            GameObject e = list.get(i);
            if (!e.isDead() && distance > e.distance(x, y) && e != exceptFor) {
                distance = (float) e.distance(x, y);
                s = (Sprite) list.get(i);
            }
        }
        return s;
    }

    public List<Sprite> getEnemiesInRange(float x, float y, float r) {
        return getSpritesInRange(enemies, x, y, r);
    }

    public List<Sprite> getHeroesInRange(float x, float y, float r) {
        return getSpritesInRange(heroes, x, y, r);
    }

    private List<Sprite> getSpritesInRange(List<GameObject> list, float x, float y, float r) {
        List<Sprite> values = new ArrayList<Sprite>();

        for (GameObject s : list) {
            if (!s.isDead() && s.distance(x, y) <= r) {
                values.add((Sprite) s);
            }
        }

        return values;
    }


    public List<GameObject> getHeroes() {
        return heroes;
    }

    public List<GameObject> getEnemies() {
        return enemies;
    }

/*
 * other
 */

    private boolean isOnPlayground(int x, int y) {
        if (x > playground.left && x < playground.right &&
                y > playground.top && y < playground.bottom)
            return true;

        return false;
    }


    //						voids from SurfaceHolder.Callback

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (Toolbox.TEST_MODE)
            Log.d("GameView", "surfaceCreated");
        gameLogic = new GameLogic(getHolder(), this);
        gameLogic.start();
        gameLogic.setGameState(GameLogic.RUNNING);

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (Toolbox.TEST_MODE)
            Log.d("GameView", "surfaceChanged");
        gameLogic.setGameState(GameLogic.RUNNING);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (Toolbox.TEST_MODE)
            Log.d("GameView", "surfaceDestroyed");
        gameLogic.setGameState(GameLogic.PAUSE);
    }

    public void pause() {
        pause = true;
    }

    public void pauseMusic() {
        if (mediaPlayer.isPlaying()) ;
        mediaPlayer.pause();
    }

    public void resume() {
        pause = false;
        if (gameLogic != null)
            gameLogic.setGameState(GameLogic.RUNNING);
    }

    public void resumeMusic() {
        if (mediaPlayer != null && playMusic)
            mediaPlayer.start();
    }


    public void loadData(XMLLevelInfo levelInfo, int level) {
        this.levelInfo = levelInfo;

        if (Toolbox.TEST_MODE)
            Log.d(tag, "LEVEL ALL " + levelInfo.getData());
        this.level = level;

        //default settings
        //playground(for sprites
        dst = new Rect(0, 0, Toolbox.screenWidth, Toolbox.screenHeight);
        playground = new Rect(0, Toolbox.screenHeight / 4, Toolbox.screenWidth, Toolbox.screenHeight);

        loadBackgroundAndMusic(levelInfo.getBackgroundType());

        //hero spacing
        if (Toolbox.TEST_MODE)
            Log.d(tag, "TB START");
        for (int i = 1; i < 4; i++)
            addHero(HeroDB.getHero(i));

        int bs = buttonSize;
        int bs2 = (int) (bs * 0.9f);
        //buttons
        switch_hero.add(new ButtonV(bs / 2, bs / 2, bs2, bs2, R.drawable.hero_icon));
        switch_hero.add(new ButtonV(bs / 2 * 3, bs / 2, bs2, bs2, R.drawable.hero_icon).setPaintFilter(Color.BLUE));
        switch_hero.add(new ButtonV(bs / 2 * 5, bs / 2, bs2, bs2, R.drawable.hero_icon).setPaintFilter(Color.GREEN));

        //setlifebars
        for (int i = 0; i < DataBase.HEROES_COUNT; i++) {
            Hero h = (Hero) heroes.get(i);
            LifeBar l = new LifeBar(h);
            l.setAnchor(null);
            l.setSize((int) (buttonSize * 0.9f));
            ButtonV b = switch_hero.get(i);
            b.setLifebar(l);
            h.setButtonLifeBar(l);//references for updating!
        }

        switch_hero.get(0).focus();

        int rc = Toolbox.screenWidth - bs;//on the right from pause button
        //skills
        //add hero's spells
        spells = new ArrayList<List<SpellButton>>();

        boolean giveAll = false;
        // hero 1
        int[] cLevels = DataBase.getHeroLevels(1);
        Sprite parent = (Sprite) heroes.get(0);
        List<SpellButton> list = new ArrayList<SpellButton>();
        if (cLevels[3] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H1_S1, parent, cLevels[3]));
        if (cLevels[4] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2 - bs, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H1_S2, parent, cLevels[4]));
        if (cLevels[5] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2 - bs * 2, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H1_S3, parent, cLevels[5]));
        spells.add(list);

        // hero 2
        cLevels = DataBase.getHeroLevels(2);
        parent = (Sprite) heroes.get(1);
        list = new ArrayList<SpellButton>();
        if (cLevels[3] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H2_S1, parent, cLevels[3]));
        if (cLevels[4] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2 - bs, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H2_S2, parent, cLevels[4]));
        if (cLevels[5] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2 - bs * 2, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H2_S3, parent, cLevels[5]));
        spells.add(list);

        // hero 3
        cLevels = DataBase.getHeroLevels(3);
        parent = (Sprite) heroes.get(2);
        list = new ArrayList<SpellButton>();
        if (cLevels[3] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H3_S1, parent, cLevels[3]));
        if (cLevels[4] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2 - bs, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H3_S2, parent, cLevels[4]));
        if (cLevels[5] > 0 || giveAll)
            list.add(new SpellButton(rc - bs / 2 - bs * 2, bs / 2, bs2, bs2,
                    TaskDB.SPECIAL_H3_S3, parent, cLevels[5]));
        spells.add(list);


        //buttons
        pauseButton = new ButtonV(Toolbox.screenWidth - bs / 2, bs / 2, bs / 2, bs / 2, R.drawable.pause);
        pauseButton.setIs_on(true);

        musicButton = new ButtonV(Toolbox.screenWidth - bs / 2, bs / 2 * 3, bs / 2, bs / 2, R.drawable.sounds);
        musicButton.setIs_on(playMusic);

        soundButton = new ButtonV(Toolbox.screenWidth - bs / 2, bs / 2 * 5, bs / 2, bs / 2, R.drawable.sounds);
        soundButton.setIs_on(playSounds);

        musicText = new Text(getMusicStateString());
        musicText.setPosition(musicButton.getX() - bs / 2, musicButton.getY() + musicText.getTextSize() / 2);
        musicText.setDeltaAlpha(0);//stay on screen
        musicText.setTextPosition(Align.RIGHT);
        musicText.setColor(Color.WHITE);

        soundText = new Text(getSoundsStateString());
        soundText.setPosition(soundButton.getX() - bs / 2, soundButton.getY() + soundText.getTextSize() / 2);
        soundText.setDeltaAlpha(0);//stay on screen
        soundText.setTextPosition(Align.RIGHT);
        soundText.setColor(Color.WHITE);

        if (Toolbox.TEST_MODE)
            Log.d(tag, "TB END");
    }

    /**
     * load type of bacground music and bitmap
     *
     * @param backgroundType
     */
    private void loadBackgroundAndMusic(int backgroundType) {
        LevelFrame l = LevelDB.getLevelBackgroundAndMusic(backgroundType);
        backroundBmp = BitmapFactory.decodeResource(getResources(), l.backgroundId);
        bgStartWid = backroundBmp.getWidth();
        bgStartHei = backroundBmp.getHeight();

        mediaPlayer = MediaPlayer.create(getContext(), l.musicId);
        mediaPlayer.setLooping(true);
        if (playMusic)
            mediaPlayer.start();
    }


    /**
     * text in pause menu
     *
     * @return
     */
    private String getMusicStateString() {
        if (playMusic)
            return getStringR(R.string.menu_music_on);
        return getStringR(R.string.menu_music_off);
    }

    /**
     * text in pause menu
     *
     * @return
     */
    private String getSoundsStateString() {
        if (playSounds)
            return getStringR(R.string.menu_sound_on);
            return getStringR(R.string.menu_sound_off);
    }

	
	/*
	 * adding/removing layers
	 */


    private void addHero(Hero h) {
        pivots.add(h.getPivot());
        sprites.add(h);
        heroes.add(h);
    }

    private void addEnemy(Enemy e) {
        sprites.add(e);
        enemies.add(e);
        Log.d(tag, "New Enemy added! " + e.getPosition());
    }

    private void removeEnemy(Sprite s) {
        s.freeMemory();
        sprites.remove(s);
        enemies.remove(s);
        System.gc();//garbage collector
    }

    public void addWave(Wave w) {
        ground.add(w);
    }

    public void addFillScreen(FillScreen fs) {
        firstPlan.add(fs);
    }

    public void addText(Text t) {
        firstPlan.add(t);
    }

    public void addText(String s) {
        Text t = new Text(s, Toolbox.screenHeight / 8);
        t.setPosition(Toolbox.screenWidth / 2, Toolbox.screenHeight / 2);
        t.setDeltaAlpha(40);
        t.setDeltaSize(0.05f);
        firstPlan.add(t);
    }

    public void addText(String s, int color) {
        Text t = new Text(s, Toolbox.screenHeight / 8);
        t.setPosition(Toolbox.screenWidth / 2, Toolbox.screenHeight / 2);
        t.setDeltaAlpha(40);
        t.setDeltaSize(0.05f);
        t.setColor(textColor);
        firstPlan.add(t);
    }

    public void addText(int resId) {
        addText(getContext().getResources().getString(resId));
    }

    public String getStringR(int resId) {
        return getContext().getResources().getString(resId);
    }


    /**
     * if sprite is dead program have to delete references from this object
     *
     * @param sprite
     */
    public void spriteIsDead(Sprite sprite) {
        if (sprite instanceof Enemy) {
            gainedExp += ((Enemy) sprite).getExperience();
        } else if (sprite instanceof Hero) {
            addText(getContext().getResources().getString(R.string.game_hero_died));
            aliveHeroes--;
            if (aliveHeroes == 0) {
                canControl = false;
                finishedLevel = true;
                stateLose = true;

                switch_hero.get(choose_mover).unfocus();
            } else { //check if changing hero is needed
                if (heroes.get(choose_mover).isDead()) {
                    //we need to change hero
                    for (int i = 0; i < heroes.size(); i++) {
                        if (!heroes.get(i).isDead()) {
                            choose_mover = i;

                            for (ButtonV b : switch_hero)
                                b.unfocus();

                            ButtonV b = switch_hero.get(choose_mover);
                            b.isClicked(b.getX(), b.getY());
                            b.focus();

                            addWaveUnderCurrentHero();
                            break;
                        }
                    }
                }
            }
        }

        for (GameObject s : sprites) {
            if (s instanceof Sprite)
                ((Sprite) s).spriteIsDead(sprite);

        }

        sprite.deadAnimation();

    }

    public void showLoseDialog() {
        getHandler().post(new Runnable() {
            public void run() {
                GameActivity.act.get().showLoseDialog();

            }
        });

    }


    public void onLowMemory() {
        scaleLowMemory -= 0.1f;
        if (scaleLowMemory < 0.1)
            scaleLowMemory = 0.1f;

        Toolbox.onLowMemoryBmpFrames(scaleLowMemory);
        backroundBmp = Toolbox.onLowMemoryBitmap(backroundBmp, scaleLowMemory, bgStartWid, bgStartHei);
    }

    /**
     * recycle all bitmaps. Also deletes database in Toolbox. After this function GameView canvas will be empty
     */
    public void freeMemory() {

        memoryFree = true;
        //sprites
        for (GameObject s : sprites)
            s.freeMemory();

        backroundBmp.recycle();

        //buttons
        for (int i = 0; i < spells.size(); i++) {
            List<SpellButton> list = spells.get(i);
            for (int j = 0; j < list.size(); j++)
                list.get(j).freeMemory();
        }
        pauseButton.freeMemory();
        musicButton.freeMemory();
        soundButton.freeMemory();
        for (ButtonV b : switch_hero)
            b.freeMemory();

        GameView.game = null;
        //GameView.context = null;

        Toolbox.freeMemoryAll();
        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();

            mediaPlayer.release();
        }

    }

    /**
     * async task for reading next section of leveldata
     *
     * @author Zientara
     */
    class ReadSectionAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // nthg
        }


        protected String doInBackground(String... args) {
            if (levelInfo.getData().size() > 0) {
                if (Toolbox.TEST_MODE)
                    Log.d(tag, "Reading next section");
                List<XMLData> data = levelInfo.getData().get(0);
                levelInfo.getData().remove(0);

                //read data
                for (XMLData d : data) {

                    if (d instanceof XMLEnemy) {
                        XMLEnemy e = (XMLEnemy) d;
                        sectionSprites.add(EnemyDB.getEnemy(e.getEnemyId(), e.getLevel()));
                    } else if (d instanceof XMLText) {
                        XMLText t = (XMLText) d;
                        textData = TextDB.getText(t.getTextId());
                        textColor = t.getColor();
                    } else if (showHints && d instanceof XMLHint) {
                        hintData = (XMLHint) d;
                    }

                }
                if (Toolbox.TEST_MODE)
                    Log.d(tag, "Size of new wave is " + sectionSprites.size());
            } else {
                if (Toolbox.TEST_MODE)
                    Log.d(tag, "FINISH no more sections");
                finishedLevel = true;
                endAnimation();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            if (finishedLevel) {
                addText(R.string.game_win);
                if (Toolbox.TEST_MODE)
                    Log.d(tag, "Win!");
            }

            if (textData != null) {
                addText(textData, textColor);
                textData = null;
            }
            readingSection = false;
        }

    }

    private void endAnimation() {
        float x = Toolbox.screenWidth / 4;
        float y = Toolbox.screenHeight / 3 * 2;
        canControl = false;

        aliveHeroes = 0;

        for (int i = 0; i < DataBase.HEROES_COUNT; i++) {
            Hero h = (Hero) heroes.get(i);
            if (!h.isDead()) {
                float speed = (float) (h.distance(x * (i + 1), y) / endTimeFirst);
                h.changeDestination(x * (i + 1), y, speed, false);
                h.showLifeBar();
                getExp[i] = true;
                aliveHeroes++;
            }
        }


        for (ButtonV b : switch_hero) {
            b.fadeOutAnimation();
        }

        pauseButton.fadeOutAnimation();

        touchPivot.hide();

        List<SpellButton> spel = spells.get(choose_mover);
        for (SpellButton b : spel)
            b.fadeOutAnimation();
    }

    private void endAnimationSecond() {
        for (int i = 0; i < DataBase.HEROES_COUNT; i++) {
            if (getExp[i]) {
                ((Sprite) heroes.get(i)).changeDestination(Toolbox.screenWidth * 2, Toolbox.screenHeight / 3 * 2, 0, false);
            }
        }
    }

    private void endAnimationThird() {
        getHandler().post(new Runnable() {
            public void run() {
                GameActivity.act.get().showWinDialog();
            }
        });
    }

    private void saveProgress() {
        expPerHero = gainedExp / aliveHeroes;

        // give exp
        String s1 = getStringR(R.string.game_exp1);
        String s2 = getStringR(R.string.game_exp2);

        for (int i = 1; i < DataBase.HEROES_COUNT + 1; i++) {
            if (getExp[i - 1]) {
                DataBase.addHeroExp(i, expPerHero);
                Text t = new Text(s1 + expPerHero + s2);
                Hero h = (Hero) heroes.get(i - 1);
                t.setColor(Color.YELLOW);
                t.setDeltaAlpha(50);
                t.setPosition(h.getX(), h.getY() - h.getHeight() * 1.2f);
                addText(t);
            }
        }
        DataBase.setLevelStars(level, aliveHeroes);
        DataBase.unlockLevel(level + 1);
    }

    private void lose() {
        showLoseDialog();
    }


    public void addMissle(Missle m) {
        sprites.add(m);
    }


    public void postFPS(float avFps) {
        fps = avFps;
    }
    
}
