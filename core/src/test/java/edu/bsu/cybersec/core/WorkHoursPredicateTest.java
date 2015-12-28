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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public final class WorkHoursPredicateTest {


    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, true},
                {12, false},
                {24, true},
                {36, false}
        });
    }

    private WorkHoursPredicate pred;

    @Parameterized.Parameter
    public int elapsedHours;

    @Parameterized.Parameter(value = 1)
    public boolean expected;

    @Before
    public void setUp() {
        pred = WorkHoursPredicate.instance();
    }

    @Test
    public void testApply() {
        assertEquals(expected, pred.apply(elapsedHours * ClockUtils.SECONDS_PER_HOUR));
    }
}
