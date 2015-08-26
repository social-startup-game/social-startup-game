package edu.bsu.cybersec.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class TimeElapseSystemTest {

    private GameWorld world;
    private PlayNClockUtil clockUtil;
    private TimeElapseSystem system;
    private int gameClockEntityId;

    @Before
    public void setUp() {
        world = new GameWorld();
        clockUtil = new PlayNClockUtil(world);
        system = world.timeElapseSystem;
        gameClockEntityId = system.simClockEntity.id;
    }


    @Test
    public void testUpdate_noElapsedTime_noChange() {
        clockUtil.advance(0);
        thenElapsedMsIs(0);
    }

    private void thenElapsedMsIs(int expected) {
        assertEquals(expected, world.simClock.get(gameClockEntityId).elapsedMS);
    }

    @Test
    public void testUpdate_oneSimSecondPerClockSecond_oneSecondElapses() {
        final int elapsedMS = 1000;
        clockUtil.advance(elapsedMS);
        thenElapsedMsIs(elapsedMS);
    }

    @Test
    public void testUpdate_twoSimSecondsPerClockSecond_twoSecondsElapse() {
        system.setSimSecondsPerClockSecond(2);
        clockUtil.advance(1000);
        thenElapsedMsIs(2000);
    }

    @Test
    public void testUpdate_noElapsedTime_tickIsUnchanged() {
        thenTickIs(0);
    }

    private void thenTickIs(int i) {
        assertEquals(i, world.simClock.get(gameClockEntityId).tickMS);
    }

    @Test
    public void testUpdate_oneSecondElapsed_tickIsOneSecond() {
        clockUtil.advance(1000);
        thenTickIs(1000);
    }

    @Test
    public void testUpdate_oneSecondElapsedTwice_tickIsTwoSeconds() {
        clockUtil.advance(1000);
        clockUtil.advance(1000);
        thenTickIs(2000);
    }

    @Test
    public void testAdvance_zero_sameTime() {
        system.advance(0);
        thenElapsedMsIs(0);
    }

    @Test
    public void testAdvance_oneSecond() {
        final int oneSecond = 1000;
        system.advance(oneSecond);
        thenElapsedMsIs(oneSecond);
    }
}
