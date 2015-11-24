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
import tripleplay.entity.Entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AvailablePredicateTest {
    private AvailablePredicate pred;
    private GameWorld world;
    private Entity worker;

    @Before
    public void setUp() {
        this.world = new GameWorld();
        pred = new AvailablePredicate(world);
    }

    @Test
    public void testApply_available() {
        givenAWorkerDoing(Task.DEVELOPMENT);
        assertTrue(pred.apply(worker));
    }

    private void givenAWorkerDoing(Task task) {
        worker = world.create(true).add(world.tasked);
        world.tasked.set(worker.id, task);
    }

    @Test
    public void testApply_unavailable() {
        givenAWorkerDoing(new Task("Arbitrary name") {
            @Override
            public boolean isReassignable() {
                return false;
            }
        });
        assertFalse(pred.apply(worker));
    }
}
