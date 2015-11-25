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

import com.google.common.collect.ImmutableList;
import edu.bsu.cybersec.core.ClockUtils;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import tripleplay.entity.Entity;

import java.util.List;

public class DataStolenNotifyChoiceEvent extends NarrativeEvent {

    private static final float PERCENT_LOSS_ON_NOTIFY = 0.05f;
    private static final int HOURS_UNTIL_DISCOVERY_AFTER_IGNORE = 6;
    private static final float PERCENT_LOSS_ON_IGNORE = 0.50f;

    public DataStolenNotifyChoiceEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        return "It looks like some of your user data was stolen by hackers! What do you do?";
    }

    @Override
    public List<? extends Option> options() {
        return ImmutableList.of(new NotifyOption(), new IgnoreOption());
    }

    private class NotifyOption implements Option {
        @Override
        public String text() {
            return "Notify our users";
        }

        @Override
        public void onSelected() {
            Entity e = world.create(true).add(world.event, world.timeTrigger);
            world.timeTrigger.set(e.id, world.gameTime.get().now + ClockUtils.SECONDS_PER_HOUR);
            world.event.set(e.id, new NarrativeEvent(world) {
                float loss;

                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new OkOption());
                }

                @Override
                public void run() {
                    final float users = world.users.get();
                    loss = users * PERCENT_LOSS_ON_NOTIFY;
                    world.users.update(users - loss);
                    super.run();
                }

                @Override
                public String text() {
                    return (int) loss + " users have left our service after hearing about how hackers stole some of their personal information.";
                }
            });
        }
    }

    private class IgnoreOption implements Option {
        @Override
        public String text() {
            return "Ignore it";
        }

        @Override
        public void onSelected() {
            Entity e = world.create(true).add(world.event, world.timeTrigger);
            world.timeTrigger.set(e.id, world.gameTime.get().now + ClockUtils.SECONDS_PER_HOUR * HOURS_UNTIL_DISCOVERY_AFTER_IGNORE);
            world.event.set(e.id, new NarrativeEvent(world) {
                float loss;

                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new OkOption());
                }

                @Override
                public void run() {
                    final float users = world.users.get();
                    loss = users * PERCENT_LOSS_ON_IGNORE;
                    world.users.update(users - loss);
                    super.run();
                }

                @Override
                public String text() {
                    return "An independent security expert discovered that you ignored a data breach and has informed the press! " + (int) loss + " users have left your service after finding out that you did not notify them of the problem.";
                }
            });
        }
    }
}
