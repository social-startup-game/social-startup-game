/*
 * Copyright 2015 Paul Gestwicki
 *
 * This file is part of The Social Startup Game
 *
 * The Social Startup Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Social Startup Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Social Startup Game.  If not, see <http://www.gnu.org/licenses/>.
 */

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
