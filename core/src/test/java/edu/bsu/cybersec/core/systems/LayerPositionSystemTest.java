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

package edu.bsu.cybersec.core.systems;

import org.junit.Test;
import playn.core.Surface;
import playn.scene.Layer;
import pythagoras.f.Point;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public class LayerPositionSystemTest extends AbstractSystemTest {

    @Override
    public void setUp() {
        super.setUp();
        new LayerPositionSystem(world);
    }

    @Test
    public void testUpdate_setsXY() {
        Layer layer = makeUnrenderedLayerForTesting();
        final Point desiredLocation = new Point(10, 10);
        final Entity e = world.create(true).add(world.sprite, world.position);
        world.sprite.set(e.id, layer);
        world.position.set(e.id, desiredLocation);
        whenSomeTimeElapses();
        final Point actual = new Point();
        layer.translation(actual);
        assertEquals(desiredLocation, actual);
    }

    private Layer makeUnrenderedLayerForTesting() {
        return new Layer() {
            @Override
            protected void paintImpl(Surface surf) {
                // Ignore; this is just for testing
            }
        };
    }
}
