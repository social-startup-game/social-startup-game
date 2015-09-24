package edu.bsu.cybersec.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class GameTimeSystemTest extends AbstractSystemTest {

    private GameTimeSystem system;

    @Override
    public void setUp() {
        super.setUp();
        system = new GameTimeSystem(world);
    }

    @Test
    public void testGameTimeInitializedToNonZeroValue() {
        assertTrue("Game time should be initialized to a positive value, but it was " + world.gameTimeMs,
                world.gameTimeMs > 0);
    }

    @Test
    public void testScale_oneMs() {
        system.setScale(10);
        advanceOneMillisecond();
        assertEquals(10, world.gameTimeMs);
    }

    @Test
    public void testSystemEmitsSignalsOnClockPulse() {

    }
}
