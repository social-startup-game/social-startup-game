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

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class RandomInRangeTest {

    private static final int TRIALS = 1000;

    @Test
    public void testNextInt_allValuesInRange() {
        Range<Integer> range = Range.closed(1, 4);
        RandomInRange rir = new RandomInRange(1, 4);
        for (int i = 0; i < TRIALS; i++) {
            int number = rir.nextInt();
            assertTrue(range.contains(number));
        }
    }

    @Test
    public void testNextInt_allValuesGenerated() {
        Set<Integer> set = Sets.newHashSet();
        RandomInRange rir = new RandomInRange(1, 4);
        for (int i = 0; i < TRIALS; i++) {
            int number = rir.nextInt();
            set.add(number);
        }
        for (int i = 1; i <= 4; i++) {
            assertTrue("Missing value " + i, set.contains(i));
        }
    }
}
