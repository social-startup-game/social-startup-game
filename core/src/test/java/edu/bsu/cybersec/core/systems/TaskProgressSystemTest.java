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

import edu.bsu.cybersec.core.ClockUtils;
import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public final class TaskProgressSystemTest extends AbstractSystemTest {

    private static final int FAKE_TASK_ID = -1;
    private static final String INITIAL_TASK_NAME = "Test Task";

    private Entity workerEntity;
    private Entity taskEntity;

    @Override
    public void setUp() {
        super.setUp();
        new TaskProgressSystem(world);
    }

    @Test
    public void testUpdate_noChangeInTimeRemainingWhenNotBeingWorkedOn() {
        final int initialSecondsRemaining = 1000;
        givenAWorkerAndATaskRequiring(initialSecondsRemaining);
        whenOneHourOfGameTimeElapses();
        assertEquals("Time remaining should not have changed",
                initialSecondsRemaining, world.secondsRemaining.get(taskEntity.id));
    }

    private void givenAWorkerAndATaskRequiring(int initialSecondsRemaining) {
        workerEntity = makeWorker();
        taskEntity = world.create(true).add(world.name, world.owner, world.secondsRemaining);
        world.owner.set(taskEntity.id, workerEntity.id);
        world.secondsRemaining.set(taskEntity.id, initialSecondsRemaining);
        world.name.set(taskEntity.id, INITIAL_TASK_NAME);
    }

    private Entity makeWorker() {
        Entity e = world.create(true).add(world.task);
        world.task.set(e.id, FAKE_TASK_ID);
        return e;
    }

    @Test
    public void testUpdate_timeRemainingDecreasesWhenBeingWorkedOn() {
        final int initialSecondsRemaining = 1000;
        givenAWorkerAndATaskRequiring(initialSecondsRemaining);
        givenTheWorkerIsWorkingOnTheTask();
        whenOneHourOfGameTimeElapses();
        assertTrue("Seconds remaining has decreased",
                world.secondsRemaining.get(taskEntity.id) < initialSecondsRemaining);
    }

    private void givenTheWorkerIsWorkingOnTheTask() {
        world.task.set(workerEntity.id, taskEntity.id);
    }

    @Test
    public void testUpdate_onCompleteIsCalledWhenComplete() {
        final int initialSecondsRemaining = ClockUtils.SECONDS_PER_HOUR;
        givenAWorkerAndATaskRequiring(initialSecondsRemaining);
        givenTheWorkerIsWorkingOnTheTask();

        Runnable onComplete = mock(Runnable.class);
        taskEntity.add(world.onComplete);
        world.onComplete.set(taskEntity.id, onComplete);

        whenOneHourOfGameTimeElapses();
        verify(onComplete, times(1)).run();
    }

    @Test
    public void testUpdate_adjustsName() {
        givenAWorkerAndATaskRequiring(1000);
        givenTheWorkerIsWorkingOnTheTask();
        whenOneHourOfGameTimeElapses();
        assertNotEquals("Name should have been updated", INITIAL_TASK_NAME, world.name.get(taskEntity.id));
    }

    @Test
    public void testUpdateName() {
        String newName = TaskProgressSystem.updateName("Foo (3hr)", 2);
        assertEquals("Foo (2hr)", newName);
    }
}
