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
import edu.bsu.cybersec.core.*;
import tripleplay.entity.Entity;

import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class DefaultNarrativeScript {

    private final Random random = new Random();
    private GameWorld world;

    public void createIn(GameWorld world, GameConfig config) {
        this.world = checkNotNull(world);
        if (!config.skipWelcome()) {
            now().runEvent(new WelcomeEvent(world));
        }
        for (NarrativeEvent event : makeEventListIn(world)) {
            int time = generateEventTime();
            atSeconds(time).runEvent(event);
        }
    }

    private static List<NarrativeEvent> makeEventListIn(GameWorld world) {

        List<NarrativeEvent> list = Lists.newArrayList();
        list.add(new ScriptKiddieAttackEvent(world));
        list.add(new SecurityConferenceEvent(world));
        list.add(new DataStolenNotifyChoiceEvent(world));
        list.add(new InputSanitizationEvent(world));
        list.add(new DDOSEvent(world));
        list.add(new ChildAdviceEvent(world));
        list.add(new InsecurePasswordEvent(world));
        Shuffler.shuffle(list);
        return list;
    }

    private int generateEventTime() {
        final WorkHoursPredicate pred = WorkHoursPredicate.instance();
        final int endTime = world.gameEnd.get();
        checkState(endTime > 0, "End time has not been properly initialized");
        int time = random.nextInt(endTime);
        while (!pred.apply(time)) {
            time = random.nextInt(endTime);
        }
        return time;
    }

    private TimedEventBuilder now() {
        return new TimedEventBuilder(world.gameTime.get().now);
    }

    private TimedEventBuilder atSeconds(int seconds) {
        return new TimedEventBuilder(world.gameTime.get().now).addSeconds(seconds);
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

        public TimedEventBuilder addSeconds(int seconds) {
            trigger += seconds;
            return this;
        }
    }
}
