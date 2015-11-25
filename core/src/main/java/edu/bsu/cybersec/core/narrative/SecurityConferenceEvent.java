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

import java.util.Collection;
import java.util.List;

public class SecurityConferenceEvent extends NarrativeEvent {

    private static final int CONFERENCE_SKILL_INCREASE = 10;
    private static final int CONFERENCE_DURATION = 48;

    public SecurityConferenceEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        return "One of your workers can go to a two-day conference about security engineering. Whom do you send?";
    }

    @Override
    public List<Option> options() {
        List<Option> result = Lists.newArrayList();
        Collection<Entity> availableWorkers = availableWorkers();
        for (Entity e : availableWorkers) {
            result.add(new SendWorkerToConferenceOption(e.id));
        }
        result.add(new Option.DoNothingOption("No one"));
        return result;
    }


    private final class SendWorkerToConferenceOption implements Option {
        private final int id;

        SendWorkerToConferenceOption(int id) {
            this.id = id;
        }

        @Override
        public String text() {
            return world.profile.get(id).firstName;
        }

        @Override
        public void onSelected() {
            final int conferenceEnd = world.gameTime.get().now + ClockUtils.SECONDS_PER_HOUR * CONFERENCE_DURATION;
            Task task = Task.createTask("At conference")
                    .expiringAt(conferenceEnd)
                    .inWorld(world)
                    .build();
            world.tasked.set(id, task);
            after(CONFERENCE_DURATION).post(new NarrativeEvent(world) {
                @Override
                public String text() {
                    return world.profile.get(id).firstName + " has returned from the conference with greatly increased maintenance skill!";
                }

                @Override
                public void run() {
                    world.maintenanceSkill.add(id, CONFERENCE_SKILL_INCREASE);
                    world.tasked.set(id, Task.MAINTENANCE);
                    super.run();
                }
            });
        }
    }
}
