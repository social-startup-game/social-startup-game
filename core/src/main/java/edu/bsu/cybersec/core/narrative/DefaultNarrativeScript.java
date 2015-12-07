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
import tripleplay.entity.Entity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultNarrativeScript {

    private GameWorld world;

    private List<NarrativeEvent> events = new ArrayList<>();
    private int eventTime = 0;
    private final int MINIMUM_HOURS_BETWEEN_EVENTS = 12;
    private final int MAXIMUM_HOURS_BETWEEN_EVENTS = 67;
    private final RandomInRange delayGenerator = new RandomInRange(MINIMUM_HOURS_BETWEEN_EVENTS, MAXIMUM_HOURS_BETWEEN_EVENTS);


    public void createIn(GameWorld world, GameConfig config) {
        this.world = checkNotNull(world);
        int gameDurationInHours = world.gameEnd.get() / ClockUtils.SECONDS_PER_HOUR;
        if (!config.skipWelcome()) {
            now().runEvent(new WelcomeEvent(world));
        }
        populateEvents();
        int eventTime = 0;
        while (!events.isEmpty() && eventTime < gameDurationInHours) {
            hour(determineEventTime()).runEvent(getRandomEvent());
        }
    }

    private void populateEvents() {
        events.add(new ScriptKiddieAttackEvent(world));
        events.add(new SecurityConferenceEvent(world));
        events.add(new DataStolenNotifyChoiceEvent(world));
        events.add(new InputSanitizationEvent(world));
        events.add(new DDOSEvent(world));
    }

    private int determineEventTime() {
        eventTime += delayGenerator.nextInt();
        return eventTime;
    }

    private TimedEventBuilder now() {
        return new TimedEventBuilder(world.gameTime.get().now);
    }

    private TimedEventBuilder hour(int hour) {
        return new TimedEventBuilder(world.gameTime.get().now).addHours(hour);
    }

    public NarrativeEvent getRandomEvent() {
        int index = new RandomInRange(0, events.size() - 1).nextInt();
        return events.remove(index);
    }

    private final class TimedEventBuilder {
        private int trigger;

        private TimedEventBuilder(int trigger) {
            this.trigger = trigger;
        }

        public void runEvent(NarrativeEvent event) {
            Entity e = world.create(true).add(world.event, world.timeTrigger);
            world.event.set(e.id, event);
            world.timeTrigger.set(e.id, trigger);
        }

        public TimedEventBuilder addHours(int hour) {
            trigger += hour * ClockUtils.SECONDS_PER_HOUR;
            return this;
        }
    }
}
