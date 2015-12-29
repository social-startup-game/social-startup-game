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

import edu.bsu.cybersec.core.*;
import org.junit.Test;
import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.*;

/**
 * Tests for {@link ScriptKiddieAttackEvent}.
 * <p/>
 * These are actually integration tests, but I do not want to fiddle with the maven settings to ensure
 * it runs at a separate time.
 */
public final class ScriptKiddieAttackEventTest {

    private GameWorld.Systematized world;
    private int initialTaskId;

    @Test
    public void testRetaliationTaskChangesWorkerTask() {
        givenAWorldWithOneWorker();
        whenRetaliationIsSelected();
        thenTheWorkersCurrentTaskIsNotHisOriginalTask();
    }

    private void givenAWorldWithOneWorker() {
        world = new GameWorld.Systematized();
        Entity firstWorker = world.create(true)
                .add(world.task, world.employeeNumber, world.profile, world.developmentSkill, world.maintenanceSkill);
        world.profile.set(firstWorker.id,
                EmployeeProfile.firstName("Bob")
                        .lastName("Ross")
                        .withDegree("Bachelors of Art")
                        .from("PBS")
                        .bio("He makes happy little trees"));
        world.workers.add(firstWorker);
        initialTaskId = world.maintenanceTaskId;
        world.task.set(firstWorker.id, initialTaskId);
    }

    private void whenRetaliationIsSelected() {
        ScriptKiddieAttackEvent event = new ScriptKiddieAttackEvent(world);
        NarrativeEvent.Option firstWorkerRetaliateOption = event.options().get(0);
        checkState(firstWorkerRetaliateOption instanceof ScriptKiddieAttackEvent.RetaliateOption,
                "Expected instance of RetaliateOption but is " + firstWorkerRetaliateOption);
        firstWorkerRetaliateOption.onSelected();
    }

    private void thenTheWorkersCurrentTaskIsNotHisOriginalTask() {
        int postOptionTaskId = world.task.get(world.workers.get(0).id);
        assertNotEquals(initialTaskId, postOptionTaskId);
    }

    @Test
    public void testRetaliationTaskChangesWorkerTaskToRetaliation() {
        givenAWorldWithOneWorker();
        whenRetaliationIsSelected();
        thenTheWorkersCurrentTaskIsRetaliation();
    }

    private void thenTheWorkersCurrentTaskIsRetaliation() {
        int postOptionTaskId = world.task.get(world.workers.get(0).id);
        assertTrue("First worker's task should be retaliation but is " + world.name.get(postOptionTaskId),
                isRetaliationTask(postOptionTaskId));
    }

    private boolean isRetaliationTask(int taskId) {
        return world.name.get(taskId).startsWith(ScriptKiddieAttackEvent.RETALIATION_TASK_LABEL);
    }

    @Test
    public void testRetaliationTaskSuspendedAfterWorkHours() {
        givenAWorldWithOneWorker();
        advanceHours(6);
        verifyThatItIsWorkHours();
        whenRetaliationIsSelected();
        advanceHours(4);
        verifyThatItIsNotWorkHours();
        int task = world.task.get(world.workers.get(0).id);
        assertFalse("Task should not be retaliation", isRetaliationTask(task));
    }

    private void advanceHours(int hours) {
        for (; hours > 0; hours--) {
            final int ms = ClockUtils.MS_PER_HOUR * hours;
            world.advanceGameTime(ms);
            Clock playnClock = new Clock();
            playnClock.dt = ms;
            playnClock.tick += ms;
            world.update(playnClock);
        }
    }

    private void verifyThatItIsWorkHours() {
        checkState(WorkHoursPredicate.instance().apply(world.gameTime.get().now), "It should be work hours.");
    }

    private void verifyThatItIsNotWorkHours() {
        int now = world.gameTime.get().now;
        checkState(!WorkHoursPredicate.instance().apply(now), "It should not be work hours but it is " + now);
    }
}
