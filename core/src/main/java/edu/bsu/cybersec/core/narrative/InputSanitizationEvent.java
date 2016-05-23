/*
 * Copyright 2016 Paul Gestwicki
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.ClockUtils;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import edu.bsu.cybersec.core.TaskFlags;
import tripleplay.entity.Entity;

import java.util.List;

public class InputSanitizationEvent extends NarrativeEvent {
    private static final String EVENT_NAME = "Sanitization";
    private static final int HOURS_FOR_SANITIZATION = 16;
    private static final float PERCENT_LOSS_ON_SANITIZATION = .07f;
    private static final float PERCENT_LOSS_ON_IGNORE = .20f;

    public InputSanitizationEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        return "One of your engineers is concerned about injection attacks: if user data is not handled correctly, hackers may be able to run their programs on your machines. Who will you assign to investigate?";
    }

    @Override
    public List<? extends Option> options() {
        List<Option> options = Lists.newArrayListWithCapacity(4);
        for (Entity e : availableWorkers()) {
            options.add(new EmployeeAssignmentOption(e.id));
        }
        options.add(new IgnoreOption());
        return options;
    }

    private final class EmployeeAssignmentOption extends Option.Terminal {
        private final int id;
        private String text = null;

        EmployeeAssignmentOption(int id) {
            this.id = id;
            text = world.profile.get(id).firstName;
        }


        @Override
        public String eventAction() {
            return EVENT_NAME;
        }

        @Override
        public String eventLabel() {
            return text;
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            assignEmployee();
        }

        private void assignEmployee() {
            final Entity taskEntity = world.create(true).add(world.name, world.owner, world.secondsRemaining, world.onComplete, world.taskFlags);
            world.name.set(taskEntity.id, "Investigating");
            world.owner.set(taskEntity.id, id);
            world.taskFlags.set(taskEntity.id, TaskFlags.flags(TaskFlags.BOUND_TO_WORKDAY));
            world.secondsRemaining.set(taskEntity.id, HOURS_FOR_SANITIZATION * ClockUtils.SECONDS_PER_HOUR);
            world.onComplete.set(taskEntity.id, new Runnable() {
                @Override
                public void run() {
                    post(new AbstractUserLossEvent(world, PERCENT_LOSS_ON_SANITIZATION) {
                        @Override
                        public List<? extends Option> options() {
                            return ImmutableList.of(new DoNothingOption("Ok"));
                        }

                        @Override
                        public String text() {
                            return "You lost " + loss + " users before you could fix the injection problem, but now it has been fixed.";
                        }

                        @Override
                        public void run() {
                            world.task.set(id, world.maintenanceTaskId);
                            super.run();
                            taskEntity.close();
                        }
                    });
                }
            });
            world.task.set(id, taskEntity.id);
        }
    }

    private final class IgnoreOption extends Option.Terminal {
        private final String text = "Nobody";

        @Override
        public String eventAction() {
            return EVENT_NAME;
        }

        @Override
        public String eventLabel() {
            return text;
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            after(HOURS_FOR_SANITIZATION).post(new AbstractUserLossEvent(world, PERCENT_LOSS_ON_IGNORE) {
                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new DoNothingOption("Ok"));
                }

                @Override
                public String text() {
                    return "One of your employees finds and fixes a defect regarding input handling. You lost "
                            + loss + " users while you ignored the problem.";
                }
            });

        }
    }
}
