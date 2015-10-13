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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class NameTest {

    private static final Name JOE_SCHMOE = Name.first("Joe").andLast("Shmoe");
    private static final Name JOE_SMITH = Name.first("Joe").andLast("Smith");

    @Test
    public void testEquals_identityEquality() {
        //noinspection EqualsWithItself
        assertTrue(JOE_SCHMOE.equals(JOE_SCHMOE));
    }

    @Test
    public void testEquals_unequal() {
        assertFalse(JOE_SCHMOE.equals(JOE_SMITH));
    }

}
