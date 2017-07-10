package pl.pwr.rafalz.arenagrozy.game;

import android.graphics.Canvas;

/**
 * Interface describes base important functions for game objects
 *
 * @author Evil
 */
public interface RunnableInerface {
    
    /**
     * calculate actions and moves
     *
     * @param dt
     */
    void update(float dt);

    /**
     * draws an object
     *
     * @param c
     */
    void draw(Canvas c);

    /**
     * can be deleted from list? Only if object is dead/finished animation.
     */
    boolean canBeDeleted();

    /**
     * recycle bitmaps and remove references
     */
    void freeMemory();

}
