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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ShufflerTest {

    private static final ImmutableList<Integer> INITIAL_LIST = ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Test
    public void testShuffle() {
        int timesToRun = 5;
        boolean foundAShuffledOne = false;
        for (int i = 0; i < timesToRun; i++) {
            List<Integer> list = Lists.newArrayList(INITIAL_LIST);
            Shuffler.shuffle(list);
            if (!list.equals(INITIAL_LIST)) {
                foundAShuffledOne = true;
                break;
            }
        }
        assertTrue("Shuffling should change the order of items in a list.", foundAShuffledOne);
    }
}
