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

public class ScriptKiddieAttackEvent extends NarrativeEvent {

    private static final int HOURS_UNTIL_REPERCUSSION = 24;
    private static final int HOURS_FOR_RETALIATION = 6;
    private static final float LOSS_PERCENT = 0.75f;

    public ScriptKiddieAttackEvent(GameWorld world) {
        super(world);
    }

    @Override
    public List<? extends Option> options() {
        List<Option> options = Lists.newArrayListWithCapacity(4);
        for (Entity e : availableWorkers()) {
            options.add(new RetaliateOption(e.id));
        }
        options.add(new Option.DoNothingOption("Nobody"));
        return options;
    }

    @Override
    public String text() {
        return "You were attacked by a script kiddie\u2014an amateur who copied some code from the Internet to attack our site. The attack was not successful, but we have to keep our guard up against future attacks.\n\nWould you like to try to find the attackers and strike back? Who should do it?";
    }

    private final class RetaliateOption implements Option {

        private final int id;

        public RetaliateOption(int id) {
            this.id = id;
        }

        @Override
        public String text() {
            return world.profile.get(id).firstName;
        }

        @Override
        public void onSelected() {
            assignRetaliation();
            registerRepercussion();
        }

        private void assignRetaliation() {
            final int endOfRetaliation = world.gameTime.get().now + HOURS_FOR_RETALIATION * ClockUtils.SECONDS_PER_HOUR;
            world.tasked.set(id, Task.createTask("Retaliating")
                    .expiringAt(endOfRetaliation)
                    .inWorld(world)
                    .build());
            after(HOURS_FOR_RETALIATION).post(new NarrativeEvent(world) {
                @Override
                public String text() {
                    return world.profile.get(id).firstName + " was unable to determine who the script kiddies were and returns to work.";
                }

                @Override
                public void run() {
                    world.tasked.set(id, Task.DEVELOPMENT);
                    super.run();
                }
            });
        }

        private void registerRepercussion() {
            after(HOURS_UNTIL_REPERCUSSION).post(new AbstractUserLossEvent(world, LOSS_PERCENT) {
                @Override
                public String text() {
                    return "Not only was having your employee retaliate unsuccessful, it was illegal! The FBI will be looking in to this...\n\n You lost "
                            + loss + " users because of your short temper.";
                }
            });
        }
    }
}
