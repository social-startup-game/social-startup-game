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

package edu.bsu.cybersec.core.narrative;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import edu.bsu.cybersec.core.Task;
import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ScriptKiddieAttackEventTest {

    private GameWorld.Systematized world;
    private Task initialTask;

    @Test
    public void testRetaliationTaskChangesWorkerTask() {
        givenAWorldWithOneWorker();
        whenRetaliationIsSelected();
        thenTheWorkersCurrentTaskIsNotHisOriginalTask();
    }

    private void givenAWorldWithOneWorker() {
        world = new GameWorld.Systematized();
        Entity firstWorker = world.create(true)
                .add(world.tasked, world.employeeNumber);
        world.workers.add(firstWorker);
        configureWorkerInitialTask(firstWorker);
    }

    private void configureWorkerInitialTask(Entity worker) {
        initialTask = mock(Task.class);
        when(initialTask.isBoundByWorkDay()).thenReturn(true);
        when(initialTask.isReassignable()).thenReturn(true);
        world.tasked.set(worker.id, initialTask);
    }

    private void whenRetaliationIsSelected() {
        ScriptKiddieAttackEvent event = new ScriptKiddieAttackEvent(world);
        NarrativeEvent.Option firstWorkerRetaliateOption = event.options().get(0);
        firstWorkerRetaliateOption.onSelected();
    }

    private void thenTheWorkersCurrentTaskIsNotHisOriginalTask() {
        Task postOptionTask = world.tasked.get(world.workers.get(0).id);
        assertFalse(postOptionTask.equals(initialTask));
    }

}
