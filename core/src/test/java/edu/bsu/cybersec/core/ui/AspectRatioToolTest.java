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

package edu.bsu.cybersec.core.ui;

import org.junit.Test;
import pythagoras.f.Dimension;
import pythagoras.f.Rectangle;

import static org.junit.Assert.assertEquals;

public class AspectRatioToolTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void testCreate_square_withinWideSpace() {
        AspectRatioTool tool = new AspectRatioTool(1f);
        Dimension viewSize = new Dimension(200, 100);
        Rectangle box = tool.createBoundingBoxWithin(viewSize);
        assertEquals(100, box.width(), EPSILON);
    }

    @Test
    public void testCreate_tallSkinnySpace_withinWideSpace() {
        AspectRatioTool tool = new AspectRatioTool(0.5f);
        Dimension viewSize = new Dimension(200, 100);
        Rectangle box = tool.createBoundingBoxWithin(viewSize);
        assertEquals(50, box.width(), EPSILON);
    }

    @Test
    public void testCreate_iPhone5Ratio_withinWideSpace() {
        AspectRatioTool tool = new AspectRatioTool(9 / 16f);
        Dimension viewSize = new Dimension(900, 960);
        Rectangle box = tool.createBoundingBoxWithin(viewSize);
        assertEquals(540, box.width, EPSILON);
    }
}
