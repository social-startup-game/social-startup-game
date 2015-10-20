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

import static org.junit.Assert.*;

public class GameTimeTest {

    private static final GameTime ZERO_TO_FIVE = new GameTime(5, 0);

    @Test
    public void testDelta() {
        assertEquals(5, ZERO_TO_FIVE.delta());
    }

    @Test
    public void testContains_true() {
        assertTrue(ZERO_TO_FIVE.contains(3));
    }

    @Test
    public void testContains_false() {
        assertFalse(ZERO_TO_FIVE.contains(6));
    }
}
