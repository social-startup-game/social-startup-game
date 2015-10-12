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
        givenAnEventThatTriggersInMilliseconds(1);
        whenOneMsAdvances();
        thenTheEventTriggers();
    }

    private void whenOneMsAdvances() {
        // We manually fiddle with the gameTimeMs here since the unit test runs independently of any GameTimeSystem.
        world.advanceGameTime(1);
        advanceOneMillisecond();
    }

    private void givenAnEventThatTriggersInMilliseconds(int ms) {
        Entity e = world.create(true)
                .add(world.timeTrigger, world.event);
        world.timeTrigger.set(e.id, world.gameTimeMs + ms);
        world.event.set(e.id, runnable = mock(Runnable.class));
    }

    private void thenTheEventTriggers() {
        verify(runnable).run();
    }

    @Test
    public void testEventDoesNotTriggerBeforeItsTime() {
        givenAnEventThatTriggersInMilliseconds(10);
        whenOneMsAdvances();
        thenTheEventDoesNotTrigger();
    }

    private void thenTheEventDoesNotTrigger() {
        verifyZeroInteractions(runnable);
    }

    @Test
    public void testEventDoesNotTriggerMoreThanOnce() {
        givenAnEventThatTriggersInMilliseconds(1);
        whenOneMsAdvances();
        whenOneMsAdvances();
        verify(runnable, times(1)).run();
    }
}
