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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DecimalTruncatorTest {

    private static final float A_FLOAT = 12.345f;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, "12"},
                {1, "12.3"},
                {2, "12.34"}
        });
    }

    @Parameterized.Parameter
    public int decimalPlaces;
    @Parameterized.Parameter(value = 1)
    public String expected;

    @Test
    public void testTruncate() {
        DecimalTruncator truncator = new DecimalTruncator(decimalPlaces);
        String actual = truncator.makeTruncatedString(A_FLOAT);
        assertEquals(expected, actual);
    }
}
