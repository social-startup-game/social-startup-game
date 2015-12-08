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

import edu.bsu.cybersec.core.Task;
import org.junit.After;
import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class WorkHoursSystemTest extends AbstractSystemTest {

    private Entity worker;
    private Task task;

    @Override
    public void setUp() {
        super.setUp();
        new WorkHoursSystem(world);
    }

    @After
    public void tearDown() {
        worker = null;
        task = null;
    }

    @Test
    public void testTaskUnchangedDuringNormalWorkHour() {
        givenAWorkerDoingANormalTask();
        whenOneHourOfGameTimeElapses();
        assertEquals(task, world.tasked.get(worker.id));
    }

    private void givenAWorkerDoingANormalTask() {
        task = mock(Task.class);
        when(task.isBoundByWorkDay()).thenReturn(true);
        makeWorker();
    }

    private void makeWorker() {
        worker = world.create(true).add(world.tasked, world.employeeNumber);
        world.employeeNumber.set(worker.id, 0);
        world.tasked.set(worker.id, task);
        world.workers.add(worker);
    }

    @Test
    public void testUpdate_taskChangesAtEndOfWorkday() {
        givenAWorkerDoingANormalTask();
        advanceHours(10);
        assertNotEquals(task, world.tasked.get(worker.id));
    }

    private void advanceHours(int hours) {
        for (int i = 0; i < hours; i++) {
            whenOneHourOfGameTimeElapses();
        }
    }

    @Test
    public void testUpdate_workerReturnsToPreviousTaskTheNextDay() {
        givenAWorkerDoingANormalTask();
        advanceHours(25);
        assertEquals(task, world.tasked.get(worker.id));
    }


    @Test
    public void testUpdate_taskChangesAtEndOfSecondWorkday() {
        givenAWorkerDoingANormalTask();
        advanceHours(24 + 9);
        assertNotEquals(task, world.tasked.get(worker.id));
    }

    @Test
    public void testUpdate_taskDoesNotEndAtEndOfDay_taskNotOverwritten() {
        givenAWorkerDoingATaskNotBoundByTheWorkday();
        advanceHours(9);
        assertEquals(task, world.tasked.get(worker.id));
    }

    private void givenAWorkerDoingATaskNotBoundByTheWorkday() {
        task = mock(Task.class);
        when(task.isBoundByWorkDay()).thenReturn(false);
        makeWorker();
    }

}
