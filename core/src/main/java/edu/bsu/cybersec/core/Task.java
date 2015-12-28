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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import react.Value;
import react.ValueView;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Task {

    public static final Task MAINTENANCE = createTask("Maintenance").build();
    public static final Task DEVELOPMENT = createTask("Development").build();
    public static final ImmutableList<Task> CORE_TASKS = ImmutableList.of(DEVELOPMENT, MAINTENANCE);

    public static TaskBuilder createTask(String name) {
        return new TaskBuilder(name);
    }

    public static final class TaskBuilder {
        private final String name;
        private boolean boundByWorkday = true;
        private boolean reassignable = true;
        private TimedTaskBuilder timed;

        private TaskBuilder(String name) {
            this.name = checkNotNull(name);
        }

        public TimedTaskBuilder expiringAt(int time) {
            return new TimedTaskBuilder(time);
        }

        public TaskBuilder notBoundByWorkday() {
            boundByWorkday = false;
            return this;
        }

        public TaskBuilder unreassignable() {
            reassignable = false;
            return this;
        }

        public Task build() {
            if (timed == null) {
                return new BasicTask(this);
            } else {
                return new TimedTask(this);
            }
        }

        public final class TimedTaskBuilder {
            private final int time;
            private GameWorld world;

            private TimedTaskBuilder(int time) {
                this.time = time;
            }

            public TaskBuilder inWorld(GameWorld world) {
                this.world = checkNotNull(world);
                timed = this;
                return TaskBuilder.this;
            }
        }
    }

    public final Value<String> name;
    private final boolean boundByWorkday;

    private Task(String name, boolean boundByWorkday) {
        this.name = Value.create(name);
        this.boundByWorkday = boundByWorkday;
    }

    public abstract boolean isReassignable();

    public boolean isBoundByWorkDay() {
        return boundByWorkday;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }

    private static final class BasicTask extends Task {

        private final boolean reassignable;

        private BasicTask(TaskBuilder builder) {
            super(builder.name, true);
            this.reassignable = builder.reassignable;
        }

        @Override
        public boolean isReassignable() {
            return reassignable;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("name", name)
                    .add("reassignable", reassignable)
                    .toString();
        }
    }

    private static class TimedTask extends Task {
        private TimedTask(final TaskBuilder builder) {
            super(builder.name, builder.boundByWorkday);
            final String baseName = builder.name;
            final GameWorld world = builder.timed.world;
            final int completionTime = builder.timed.time;
            world.gameTime.connect(new ValueView.Listener<GameTime>() {
                @Override
                public void onChange(GameTime value, GameTime oldValue) {
                    int millisToCompletion = completionTime - value.now;
                    int hoursToCompletion = (millisToCompletion / ClockUtils.SECONDS_PER_HOUR) + 1;
                    name.update(baseName + " (" + hoursToCompletion + "h)");
                    if (millisToCompletion <= 0) {
                        world.gameTime.disconnect(this);
                    }
                }
            });
        }

        @Override
        public boolean isReassignable() {
            return false;
        }
    }
}
