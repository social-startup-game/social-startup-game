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

import com.google.common.collect.Maps;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SystemPriority;
import edu.bsu.cybersec.core.Task;
import edu.bsu.cybersec.core.WorkHoursPredicate;
import playn.core.Clock;
import react.Value;
import react.ValueView;
import tripleplay.entity.Entity;
import tripleplay.entity.System;

import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public final class WorkHoursSystem extends System {

    private static final float OFF_HOURS_TIME_SCALE_FACTOR = 2.5f;
    private static final Task NOT_AT_WORK = Task.createTask("Not at work").build();

    private final GameWorld world;
    private final GameTimeSystem gameTimeSystem;
    private final WorkHoursPredicate workHoursPredicate = WorkHoursPredicate.instance();

    private final HashMap<Integer, Task> previousTasks = Maps.newHashMap();
    private final Value<Boolean> isWorkHours = Value.create(true);

    public WorkHoursSystem(GameWorld world, GameTimeSystem gameTimeSystem) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = checkNotNull(world);
        this.gameTimeSystem = checkNotNull(gameTimeSystem);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.tasked);
    }

    private interface State {
        void onEnter();

        void update();
    }

    private final State IN_WORK_HOURS = new State() {

        @Override
        public void onEnter() {
            isWorkHours.update(true);
            if (thereArePreviousTasksRecorded()) {
                setWorkersToTheirPreviousTasks();
                previousTasks.clear();
            }
            gameTimeSystem.scale().update(gameTimeSystem.scale().get() / OFF_HOURS_TIME_SCALE_FACTOR);
        }

        private boolean thereArePreviousTasksRecorded() {
            return !previousTasks.isEmpty();
        }

        private void setWorkersToTheirPreviousTasks() {
            for (int i = 0, limit = entityCount(); i < limit; i++) {
                final int id = entityId(i);
                Task previousTask = previousTasks.get(id);
                if (previousTask != null) {
                    world.tasked.set(id, previousTask);
                }
            }
        }

        @Override
        public void update() {
            if (!isCurrentlyWorkHours()) {
                changeTo(OFF_WORK_HOURS);
            }
        }
    };

    private boolean isCurrentlyWorkHours() {
        return workHoursPredicate.apply(world.gameTime.get().now);
    }

    private final State OFF_WORK_HOURS = new State() {
        @Override
        public void onEnter() {
            isWorkHours.update(false);
            for (int i = 0, limit = entityCount(); i < limit; i++) {
                final int id = entityId(i);
                Task task = world.tasked.get(id);
                if (task.isBoundByWorkDay()) {
                    previousTasks.put(id, world.tasked.get(id));
                    world.tasked.set(id, NOT_AT_WORK);
                }
            }
            gameTimeSystem.scale().update(gameTimeSystem.scale().get() * OFF_HOURS_TIME_SCALE_FACTOR);
        }

        @Override
        public void update() {
            if (isCurrentlyWorkHours()) {
                changeTo(IN_WORK_HOURS);
            }
        }
    };

    private State state = IN_WORK_HOURS;

    private void changeTo(State state) {
        this.state = state;
        this.state.onEnter();
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        state.update();
    }

    public ValueView<Boolean> isWorkHours() {
        return isWorkHours;
    }
}
