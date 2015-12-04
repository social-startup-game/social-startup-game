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
import pythagoras.f.Rectangle;

import static org.junit.Assert.assertEquals;

public final class GameBoundsTest {
    private static final float EPSILON = 0.001f;

    @Test
    public void testPercentOfHeight() {
        Rectangle box = new Rectangle(0, 0, 10, 10);
        GameBounds gameBounds = new GameBounds(box);
        float actual = gameBounds.percentOfHeight(0.5f);
        assertEquals(5, actual, EPSILON);
    }
}
