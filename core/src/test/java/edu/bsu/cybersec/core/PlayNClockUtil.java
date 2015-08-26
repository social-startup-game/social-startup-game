package edu.bsu.cybersec.core;

import playn.core.Clock;

public class PlayNClockUtil {

    public static final int MS_PER_DAY = 1000 * 60 * 60 * 24;

    private GameWorld world;

    public PlayNClockUtil(GameWorld world) {
        this.world = world;
    }

    public void advance(int ms) {
        Clock playnClock = new Clock();
        playnClock.dt = ms;
        playnClock.tick += ms;
        world.update(playnClock);
    }

}
