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
import tripleplay.entity.Entity;

import static org.mockito.Mockito.*;

public final class EventTriggerSystemTest extends AbstractSystemTest {

    private Runnable runnable;

    @Override
    public void setUp() {
        super.setUp();
        new EventTriggerSystem(world);
        runnable = mock(Runnable.class);
    }

    @Test
    public void testEvenTriggersWhenTimePassesIt() {
        givenAnEventThatTriggersInSeconds(1);
        whenOneSecondOfGameTimeElapses();
        thenTheEventTriggers();
    }

    private void givenAnEventThatTriggersInSeconds(int seconds) {
        Entity e = world.create(true)
                .add(world.timeTrigger, world.event);
        world.timeTrigger.set(e.id, world.gameTime.get().now + seconds);
        world.event.set(e.id, runnable = mock(Runnable.class));
    }

    private void thenTheEventTriggers() {
        verify(runnable).run();
    }

    @Test
    public void testEventDoesNotTriggerBeforeItsTime() {
        givenAnEventThatTriggersInSeconds(10);
        whenOneSecondOfGameTimeElapses();
        thenTheEventDoesNotTrigger();
    }

    private void thenTheEventDoesNotTrigger() {
        verifyZeroInteractions(runnable);
    }

    @Test
    public void testEventDoesNotTriggerMoreThanOnce() {
        givenAnEventThatTriggersInSeconds(1);
        whenOneSecondOfGameTimeElapses();
        whenOneSecondOfGameTimeElapses();
        verify(runnable, times(1)).run();
    }
}
