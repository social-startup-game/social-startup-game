package edu.bsu.cybersec.core;

import org.junit.Before;
import org.junit.Test;
import playn.core.Clock;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public final class TimeElapseSystemTest {

    private GameWorld world;
    private TimeElapseSystem system;
    private int gameClockEntityId;

    @Before
    public void setUp() {
        world = new GameWorld();
        system = new TimeElapseSystem(world);
        gameClockEntityId = createSimClock();
    }

    private int createSimClock() {
        Entity gameClockEntity = world.create(true);
        gameClockEntity.add(world.elapsedSimMs);
        world.elapsedSimMs.set(gameClockEntity.id, 0);
        return gameClockEntity.id;
    }

    @Test
    public void testUpdate_noElapsedTime_noChange() {
        whenMsElapses(0);
        assertEquals(0, world.elapsedSimMs.get(gameClockEntityId));
    }

    private void whenMsElapses(int elapsedMS) {
        Clock playnClock = new Clock();
        playnClock.dt = elapsedMS;
        playnClock.tick += elapsedMS;
        world.update(playnClock);
    }

    @Test
    public void testUpdate_oneSimSecondPerClockSecond_oneSecondElapses() {
        final int elapsedMS = 1000;
        whenMsElapses(elapsedMS);
        assertEquals(elapsedMS, world.elapsedSimMs.get(gameClockEntityId));
    }

}
