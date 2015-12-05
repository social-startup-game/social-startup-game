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

import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultNarrativeScript {

    private GameWorld world;
    private ArrayList<NarrativeEvent> events = new ArrayList<>();

    public void createIn(GameWorld world, GameConfig config) {
        this.world = checkNotNull(world);
        if (!config.skipWelcome()) {
            now().runEvent(new WelcomeEvent(world));
        }
        populateEvents();
        hour(2).runEvent(getRandomEvent());
        hour(4).runEvent(getRandomEvent());
        hour(6).runEvent(getRandomEvent());
        hour(8).runEvent(getRandomEvent());
        hour(10).runEvent(getRandomEvent());
    }

    private void populateEvents() {
        events.add(new ScriptKiddieAttackEvent(world));
        events.add(new SecurityConferenceEvent(world));
        events.add(new DataStolenNotifyChoiceEvent(world));
        events.add(new InputSanitizationEvent(world));
        events.add(new DDOSEvent(world));
    }

    private TimedEventBuilder now() {
        return new TimedEventBuilder(world.gameTime.get().now);
    }

    private TimedEventBuilder hour(int hour) {
        return new TimedEventBuilder(world.gameTime.get().now).addHours(hour);
    }

    public NarrativeEvent getRandomEvent() {
        int index = new RandomInRange(0, events.size() - 1).nextInt();
        return events.get(index);
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
