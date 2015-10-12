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

import org.junit.After;
import org.junit.Test;
import pythagoras.f.Dimension;
import pythagoras.f.Rectangle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AspectRatioToolTest {

    private static final float EPSILON = 0.0001f;

    private AspectRatioTool tool;
    private Dimension rootSize;
    private Rectangle child;

    @After
    public void tearDown() {
        tool = null;
        rootSize = null;
        child = null;
    }

    @Test
    public void testCreateLayer_aspectRatioEqualsViewAspectRatio_childSizeEqualsRootSize() {
        givenDesiredAspectRatioIsViewAspectRatio();
        whenALayerIsCreated();
        thenChildSizeEqualsRootSize();
    }

    private void givenDesiredAspectRatioIsViewAspectRatio() {
        configureTest(2.0f, 200, 100);
    }

    private void configureTest(float desiredRatio, float viewWidth, float viewHeight) {
        tool = new AspectRatioTool(desiredRatio);
        rootSize = new Dimension(viewWidth, viewHeight);
    }

    private void whenALayerIsCreated() {
        child = tool.createBoundingBoxWithin(rootSize);
    }

    private void thenChildSizeEqualsRootSize() {
        Dimension childSize = new Dimension(child.width(), child.height());
        assertEquals(rootSize, childSize);
    }

    @Test
    public void testCreateLayer_viewWiderThanDesired_childFitsHeight() {
        givenViewWiderThanDesired();
        whenALayerIsCreated();
        assertEquals(rootSize.height(), child.height(), EPSILON);
    }

    private void givenViewWiderThanDesired() {
        configureTest(2.0f, 400, 100);
    }

    @Test
    public void testCreateLayer_viewWiderThanDesired_childIsTopAligned() {
        givenViewWiderThanDesired();
        whenALayerIsCreated();
        assertEquals(0, child.y, EPSILON);
    }

    @Test
    public void testCreateLayer_viewWiderThanDesired_childXIsPositive() {
        givenViewWiderThanDesired();
        whenALayerIsCreated();
        assertTrue(child.x > 0);
    }

    @Test
    public void testCreateLayer_viewTallerThanDesired_childFitsWidth() {
        givenViewTallerThanDesired();
        whenALayerIsCreated();
        assertEquals(rootSize.width(), child.width(), EPSILON);
    }

    @Test
    public void testCreateLayer_viewTallerThanDesired_childIsLeftAligned() {
        givenViewTallerThanDesired();
        whenALayerIsCreated();
        assertEquals(0, child.x, EPSILON);
    }

    @Test
    public void testCreateLayer_viewTallerThanDesired_childYIsPositive() {
        givenViewTallerThanDesired();
        whenALayerIsCreated();
        assertTrue(child.y > 0);
    }

    private void givenViewTallerThanDesired() {
        configureTest(2.0f, 300, 200);
    }
}
