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

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.*;

public final class TaskFormatterTest {

    private TaskFormatter formatter;

    @Before
    public void setUp() {
        formatter = new TaskFormatter();
    }

    @Test
    public void testAsString_returnsANonEmptyString() {
        String actual = formatter.format(Task.IDLE);
        assertFalse(actual.isEmpty());
    }

    @Test
    public void testAsTask_returnsTheTask() {
        String string = formatter.format(Task.IDLE);
        int task = formatter.asTask(string);
        assertEquals(Task.IDLE, task);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormat_throwsExceptionOnUnrecognizedInput() {
        final int nonTaskInteger = Integer.MIN_VALUE;
        checkState(!Task.VALUES.contains(nonTaskInteger));
        formatter.format(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAsTask_throwsExceptionOnUnrecognizedInput() {
        final String nonTaskString = "";
        formatter.asTask(nonTaskString);
    }

    @Test
    public void testAllTasksCanBeFormatted() {
        for (int task : Task.VALUES) {
            assertNotNull("There is a format code for task " + task, formatter.format(task));
        }
    }
}
