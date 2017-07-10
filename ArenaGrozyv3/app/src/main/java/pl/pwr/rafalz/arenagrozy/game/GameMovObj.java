package pl.pwr.rafalz.arenagrozy.game;

/**
 * movable game object - base for another objects. Have data and function for
 * moving but will not update position. If you extends this class you need to
 * override update(float dt).
 *
 * @author Zientara
 */
public abstract class GameMovObj extends GameObject {
    // move
    protected float speed;
    protected float speedX;
    protected float speedY;
    protected float standardSpeed;

    protected float dest_x;
    protected float dest_y;

    /**
     * set point where object should go. This function will calculate speedX,
     * speedY, speed. Use those values in update(float dt).
     *
     * @param dest_x
     * @param dest_y
     * @param aspeed
     */
    protected void changeDestination(float dest_x, float dest_y, float aspeed) {
        float speedF;
        if (aspeed == 0)
            speedF = standardSpeed;
        else
            speedF = aspeed;

        float X = dest_x - x;
        float Y = dest_y - y;

        speed = speedF;
        Y = Math.abs(Y);
        this.dest_x = dest_x;
        this.dest_y = dest_y;

        //all distance
        double d = Math.sqrt(X * X + Y * Y);

        speedY = (float) ((Y * speed) / d);
        speedX = (float) (Math.sqrt(speed * speed - speedY * speedY));

        if (dest_x < x)
            speedX = -speedX;

        if (dest_y < y)
            speedY = -speedY;
    }
}
