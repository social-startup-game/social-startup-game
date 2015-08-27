package edu.bsu.cybersec.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class TimeElapseSystemTest extends AbstractSystemTest {

    private TimeElapseSystem system;
    private int gameClockEntityId;

    @Override
    public void setUp() {
        super.setUp();
        system = new TimeElapseSystem(world);
        gameClockEntityId = system.simClockEntity.id;
    }

    @Test
    public void testUpdate_noElapsedTime_noChange() {
        thenElapsedMsIs(0);
    }

    private void thenElapsedMsIs(int expected) {
        assertEquals(expected, world.simClock.get(gameClockEntityId).elapsedMS);
    }

    @Test
    public void testUpdate_oneSimSecondPerClockSecond_oneSecondElapses() {
        final int elapsedMS = 1000;
        advanceOneSecond();
        thenElapsedMsIs(elapsedMS);
    }

    @Test
    public void testUpdate_twoSimSecondsPerClockSecond_twoSecondsElapse() {
        system.setSimSecondsPerClockSecond(2);
        advanceTwoSeconds();
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
        advanceOneSecond();
        thenTickIs(1000);
    }

    @Test
    public void testUpdate_oneSecondElapsedTwice_tickIsTwoSeconds() {
        advanceTwoSeconds();
        thenTickIs(2000);
    }

    private void advanceTwoSeconds() {
        advanceOneSecond();
        advanceOneSecond();
    }

    @Test
    public void testAdvance_zero_sameTime() {
        thenElapsedMsIs(0);
    }

    @Test
    public void testAdvance_oneSecond() {
        final int oneSecond = 1000;
        system.advance(oneSecond);
        thenElapsedMsIs(oneSecond);
    }
}
