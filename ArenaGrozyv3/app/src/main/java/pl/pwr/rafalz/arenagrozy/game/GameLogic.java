package pl.pwr.rafalz.arenagrozy.game;

import java.lang.ref.WeakReference;

import pl.pwr.rafalz.arenagrozy.view.GameView;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * background thread for updating game view
 *
 * @author Evil
 */
public class GameLogic extends Thread {

    private WeakReference<SurfaceHolder> surfaceHolder;
    private WeakReference<GameView> game;
    private int game_state;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int STOP = 3;

    private boolean pause;

    public GameLogic(SurfaceHolder surfaceHolder, GameView game) {
        super();
        this.surfaceHolder = new WeakReference<SurfaceHolder>(surfaceHolder);
        this.game = new WeakReference<GameView>(game);
    }

    public void setGameState(int gamestate) {
        this.game_state = gamestate;
    }

    public int getGameState() {
        return game_state;
    }

    public void pauseGame() {
        pause = true;
    }

    public void resumeGame() {
        pause = false;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        long deltaTime = 0;
        float delta = 0;
        // array for calculating current fps
        float[] fps = new float[10];
        int i = 0;

        Canvas canvas;

        while (game_state == RUNNING) {
            canvas = null; // remove reference
            startTime = System.currentTimeMillis();

            try {
                // get canvas for drawing
                canvas = this.surfaceHolder.get().lockCanvas();

                // check - can be null from Android Honeycomb
                if (canvas != null) {
                    synchronized (surfaceHolder.get()) {
                        try {
                            // dont use CPU all the time - save energy
                            sleep(10);
                        } catch (InterruptedException e1) {

                        }
                        if (!pause) {
                            game.get().update(delta).draw(canvas);
                        }

                        float av = 0;
                        // average fps
                        for (int j = 0; j < 10; j++) {
                            av += fps[j];
                        }
                        av /= 10;

                        game.get().postFPS(av);
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.get().unlockCanvasAndPost(canvas);
                }
            }

            deltaTime = System.currentTimeMillis() - startTime;
            delta = deltaTime / (float) 1000;

            fps[i] = 1f / delta;
            i++;
            if (i >= 10)
                i = 0;
        }

    }

}
