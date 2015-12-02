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

import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.ClockUtils;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import edu.bsu.cybersec.core.Task;
import tripleplay.entity.Entity;

import java.util.List;

public class InputSanitizationEvent extends NarrativeEvent {
    private static final int HOURS_FOR_SANITIZATION = 16;
    private static final int HOURS_UNTIL_IGNORE_NOTIFICATION = 10;
    private static final float PERCENT_LOSS_ON_SANITIZATION = .10f;
    private static final float PERCENT_LOSS_ON_IGNORE = .35f;

    public InputSanitizationEvent(GameWorld world) {
        super(world);
        final float initialNumberOfUsers = world.users.get();
    }

    @Override
    public String text() {
        return "You are receiving complaints from numerous users about their information being taken by an outside source. Upon inspection, you see that your web service input wasn’t sanitized, which allowed your user list to be stolen.\n\nWho should work on encrypting user input?";
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

    private final class EmployeeAssignmentOption implements Option {
        private final int id;
        private String text = null;

        public EmployeeAssignmentOption(int id) {
            this.id = id;
        }

        @Override
        public String text() {
            text = world.profile.get(id).firstName;
            return text;
        }

        @Override
        public void onSelected() {
            assignEmployee();
        }

        private void assignEmployee() {
            final int endOfSanitizationPeriod = world.gameTime.get().now + HOURS_FOR_SANITIZATION * ClockUtils.SECONDS_PER_HOUR;
            world.tasked.set(id, Task.createTask("Sanitizing Input")
                    .expiringAt(endOfSanitizationPeriod)
                    .inWorld(world)
                    .build());
            after(HOURS_FOR_SANITIZATION).post(new AbstractUserLossEvent(world, PERCENT_LOSS_ON_SANITIZATION) {
                @Override
                public String text() {
                    return "You lost " + loss + " users, but have sanitized input and should not run into this problem again.";
                }

                @Override
                public void run() {
                    world.tasked.set(id, Task.MAINTENANCE);
                    super.run();
                }
            });
        }
    }

    private final class IgnoreOption implements Option {
        @Override
        public String text() {
            return "Nobody";
        }

        @Override
        public void onSelected() {
            final int endOfSanitizationPeriod = world.gameTime.get().now + HOURS_UNTIL_IGNORE_NOTIFICATION * ClockUtils.SECONDS_PER_HOUR;
            after(HOURS_FOR_SANITIZATION).post(new AbstractUserLossEvent(world, PERCENT_LOSS_ON_IGNORE) {
                @Override
                public String text() {
                    return "You lost " + loss + " users. Unfortunately, you did not address this issue, and so may run into this problem again.";
                }
            });

        }
    }
}
