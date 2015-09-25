package edu.bsu.cybersec.core;

import org.junit.Test;

import static org.junit.Assert.*;

public final class GameTimeSystemTest extends AbstractSystemTest {

    private GameTimeSystem system;

    @Override
    public void setUp() {
        super.setUp();
        system = new GameTimeSystem(world);
        new UpdatingSystem(world);
    }

    @Test
    public void testScale_oneMs() {
        system.setScale(10);
        advanceOneMillisecond();
        assertEquals(10, world.gameTimeMs);
    }

    @Test
    public void testPreviousTimeIsAdvanced() {
        advanceOneMillisecond();
        advanceOneMillisecond();
        assertTrue(world.prevGameTimeMs > 0);
    }

    @Test
    public void testSystemDisable_timeStopsMoving() {
        system.setEnabled(false);
        advanceOneMillisecond();
        assertEquals(0, world.gameTimeMs);
    }

    @Test
    public void testSystemDisable_noElapsedTime() {
        system.setEnabled(false);
        advanceOneMillisecond();
        assertTrue(world.gameTimeMs == world.prevGameTimeMs);
    }

    @Test
    public void testSystemDisableAfterHavingRun_noElapsedTime() {
        advanceOneMillisecond();
        system.setEnabled(false);
        advanceOneMillisecond();
        assertTrue(world.gameTimeMs == world.prevGameTimeMs);
    }

}
