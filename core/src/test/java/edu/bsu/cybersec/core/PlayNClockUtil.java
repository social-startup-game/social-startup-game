package edu.bsu.cybersec.core;

import playn.core.Clock;

public final class PlayNClockUtil {

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
