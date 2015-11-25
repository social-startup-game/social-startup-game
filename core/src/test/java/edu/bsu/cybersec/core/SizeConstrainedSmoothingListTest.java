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

import static org.junit.Assert.*;

public class SizeConstrainedSmoothingListTest {

    private static final int CAPACITY = 10;
    private static final int ARBITRARY_INTEGER = 7;
    private SizeConstrainedSmoothingList list;

    @Before
    public void setUp() {
        list = SizeConstrainedSmoothingList.withCapacity(CAPACITY);
    }

    @Test
    public void testIsEmpty_initial() {
        assertTrue(list.isEmpty());
    }

    @Test
    public void testIsEmpty_false_afterAddingSomething() {
        list.add(0);
        assertFalse(list.isEmpty());
    }

    @Test
    public void testGet() {
        final int someValue = 5;
        list.add(someValue);
        assertEquals(someValue, list.get(0));
    }

    @Test
    public void testSize_addToCapacity_sizeIsCapacity() {
        whenAListIsFilledToCapacity();
        assertEquals(CAPACITY, list.size());
    }

    private void whenAListIsFilledToCapacity() {
        for (int i = 0; i < CAPACITY; i++) {
            list.add(i);
        }
    }

    @Test
    public void testSize_addedOneMoreThanCapacity_dropsToHalf() {
        givenAListFilledToCapacity();
        list.add(ARBITRARY_INTEGER);
        assertEquals(CAPACITY / 2 + 1, list.size());
    }

    private void givenAListFilledToCapacity() {
        whenAListIsFilledToCapacity();
    }

    @Test
    public void testGet_afterShrink_valueIsSmoothed() {
        for (int i = 0; i < CAPACITY; i++) {
            list.add(i);
        }
        list.add(CAPACITY + 1);
        int actualHalfwayValue = list.get(list.size() / 2);
        int smoothedHalfwayValue = CAPACITY / 2;
        assertEquals(smoothedHalfwayValue, actualHalfwayValue, 1f);
    }
}
