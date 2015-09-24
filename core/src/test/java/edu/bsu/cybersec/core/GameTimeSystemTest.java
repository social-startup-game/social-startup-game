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

}
