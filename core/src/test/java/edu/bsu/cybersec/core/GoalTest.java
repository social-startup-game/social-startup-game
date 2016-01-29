/*
 * Copyright 2016 Paul Gestwicki
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

public class GoalTest {
    private final Goal goal = new Goal(100);

    @Test
    public void testIsMet_false() {
        boolean isMet = goal.isMet(50);
        assertFalse(isMet);
    }

    @Test
    public void testIsMet_true() {
        boolean isMet = goal.isMet(100);
        assertTrue(isMet);
    }

    @Test
    public void testGoalVariable() {
        int expected = 100;
        int actual = goal.minimum;
        assertEquals(expected, actual);
    }
}
