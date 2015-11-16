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

import edu.bsu.cybersec.core.Updatable;
import org.junit.Test;
import org.mockito.Matchers;
import playn.core.Clock;
import tripleplay.entity.Entity;

import static org.mockito.Mockito.*;

public final class UpdatingSystemTest extends AbstractSystemTest {

    private Updatable mockUpdatable;

    @Override
    public void setUp() {
        super.setUp();
        new UpdatingSystem(world);
        mockUpdatable = mock(Updatable.class);
    }

    @Test
    public void testUpdatableThingIsUpdated() {
        givenOneUpdatableEntity();
        whenSomeTimeElapses();
        verify(mockUpdatable).update(Matchers.any(Clock.class));
    }

    private void givenOneUpdatableEntity() {
        Entity e = world.create(true)
                .add(world.onUpdate);
        world.onUpdate.set(e.id, mockUpdatable);
    }
}
