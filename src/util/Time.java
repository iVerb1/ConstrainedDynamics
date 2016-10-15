package util;

import org.lwjgl.Sys;

/**
 * Created by Jeroen van Wijgerden on 7-3-2015.
 */
public class Time {

    private long lastTime;
    private float delta;
    private Float nextDelta;

    public Time() {   }

    public void initialize() {
        this.lastTime = 1000 * Sys.getTime() / Sys.getTimerResolution();
    }

    public float getDelta() {
        return delta;
    }

    public void tick() {
        long time = 1000 * Sys.getTime() / Sys.getTimerResolution();

        if (nextDelta == null) {
            delta = ((float) (time - lastTime)) / 1000;
        }
        else {
            delta = nextDelta;
            nextDelta = null;
        }

        lastTime = time;
    }

    public void setNextDelta(float nextDelta) {
        this.nextDelta = nextDelta;
    }

}
