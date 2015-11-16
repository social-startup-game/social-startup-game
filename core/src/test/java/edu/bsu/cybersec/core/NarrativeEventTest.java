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

import edu.bsu.cybersec.core.systems.AbstractSystemTest;
import org.junit.Test;
import react.Slot;
import tripleplay.entity.Entity;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public final class NarrativeEventTest extends AbstractSystemTest {

    @Test
    public void testRun_postsEventToGameWorld() {
        NarrativeEvent event = makeTestEvent();
        Slot<NarrativeEvent> slot = mockSlot();
        world.onNarrativeEvent.connect(slot);
        event.run();
        verify(slot).onEmit(event);
    }

    private NarrativeEvent makeTestEvent() {
        return NarrativeEvent.inWorld(world).withText("").build();
    }

    @SuppressWarnings("unchecked")
    private Slot<NarrativeEvent> mockSlot() {
        return mock(Slot.class);
    }

    @Test
    public void testEmployeeSelection_allAvailable() {
        givenAWorldWithThreeWorkers();
        NarrativeEvent event = makeWorkerSelectionEvent();
        assertEquals(3, event.options().size());
    }

    private NarrativeEvent makeWorkerSelectionEvent() {
        return NarrativeEvent.inWorld(world)
                .withText("")
                .addEmployeeSelectionsFor(mock(NarrativeEvent.Action.class))
                .build();
    }

    private void givenAWorldWithThreeWorkers() {
        for (int i = 0; i < 3; i++) {
            Entity e = world.create(true).add(world.name, world.employeeNumber, world.tasked);
            world.employeeNumber.set(e.id, i);
            world.name.set(e.id, Name.first("Bob").andLast("Ross " + i));
            world.tasked.set(e.id, mockReassignableTask());
            world.workers.add(e);
        }
    }

    private Task mockReassignableTask() {
        Task task = mock(Task.class);
        when(task.isReassignable()).thenReturn(true);
        return task;
    }

    @Test
    public void testEmployeeSelection_notAllAvailableWhenOneIsOccupied() {
        givenAWorldWithThreeWorkers();
        givenOneWorkerIsOccupied();
        NarrativeEvent event = makeWorkerSelectionEvent();
        assertEquals(2, event.options().size());
    }

    private void givenOneWorkerIsOccupied() {
        Entity worker = world.workers.get(0);
        world.tasked.set(worker.id, new Task("An unreassignable task") {
            @Override
            public boolean isReassignable() {
                return false;
            }
        });
    }
}
