package pl.pwr.rafalz.arenagrozy.game;

import pl.pwr.rafalz.arenagrozy.tools.Toolbox;

import android.graphics.Canvas;

/**
 * base class for all objects in game. When you extend this class remember to
 * override functions from interface.
 *
 * @author Evil
 */
public abstract class GameObject implements RunnableInerface {
    protected float x;
    protected float y;
    protected int width;
    protected int height;

    @Override
    public boolean canBeDeleted() {
        return false;
    }

    public boolean isOnScreen() {
        return (x > 0 && x < Toolbox.screenWidth && y > 0 && y < Toolbox.screenHeight);
    }

    public double distance(float ax, float ay) {
        float x = Math.abs(this.x - ax);
        float y = Math.abs(this.y - ay);
        return Math.sqrt(x * x + y * y);
    }

    public double distance(GameObject s) {
        float x = Math.abs(this.x - s.x);
        float y = Math.abs(this.y - s.y);// aby dalo to efekt lotu ptaka
        return Math.sqrt(x * x + y * y);
    }

    public double distanceTouch(float ax, float ay) {
        float x = Math.abs(this.x - ax);
        float y = Math.abs(this.y - ay);
        return Math.sqrt(x * x + y * y);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(GameObject go) {
        if (go != null) {
            this.x = go.x;
            this.y = go.y;
        }
    }

    public boolean isDead() {
        return true;
    }

    public String getPosition() {
        return "pos" + x + "x" + y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
