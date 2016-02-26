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
    private final Goal goal = new Goal(100, .15f);

    @Test
    public void testIsMet_notEnoughUsers_false() {
        boolean isMet = goal.isMet(50, .10f);
        assertFalse(isMet);
    }

    @Test
    public void testIsMet_tooMuchExposure_false() {
        boolean isMet = goal.isMet(500, .17f);
        assertFalse(isMet);
    }


    @Test
    public void testIsMet_true() {
        boolean isMet = goal.isMet(100, .127f);
        assertTrue(isMet);
    }

    @Test
    public void testGoalUsersVariable() {
        int expected = 100;
        int actual = goal.minimumUsers;
        assertEquals(expected, actual);
    }

    @Test
    public void testGoalExposureVariable() {
        float expected = .15f;
        float actual = goal.maximumExposure;
        float delta = 0.0001f;
        assertEquals(expected, actual, delta);
    }
}
