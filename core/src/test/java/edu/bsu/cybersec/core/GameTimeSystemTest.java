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
        advancePlayNClockOneMillisecond();
        assertEquals(10, now());
    }

    private int now() {
        return world.gameTime.get().now;
    }

    @Test
    public void testPreviousTimeIsAdvanced() {
        advancePlayNClockOneMillisecond();
        advancePlayNClockOneMillisecond();
        assertTrue(previous() > 0);
    }

    private int previous() {
        return world.gameTime.get().previous;
    }

    @Test
    public void testSystemDisable_timeStopsMoving() {
        system.setEnabled(false);
        advancePlayNClockOneMillisecond();
        assertEquals(0, now());
    }

    @Test
    public void testSystemDisable_noElapsedTime() {
        system.setEnabled(false);
        advancePlayNClockOneMillisecond();
        assertTrue(now() == previous());
    }

    @Test
    public void testSystemDisableAfterHavingRun_noElapsedTime() {
        advancePlayNClockOneMillisecond();
        system.setEnabled(false);
        advancePlayNClockOneMillisecond();
        assertTrue(now() == previous());
    }

}
