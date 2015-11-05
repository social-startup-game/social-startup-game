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

import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultNarrativeScript {

    private static final Runnable DO_NOTHING = new Runnable() {
        @Override
        public void run() {
        }
    };

    private GameWorld world;

    public void createIn(GameWorld world) {
        this.world = checkNotNull(world);
        final Entity e = world.create(true)
                .add(world.timeTrigger, world.event);
        world.timeTrigger.set(e.id, world.gameTime.get().now);
        world.event.set(e.id, makeWelcomeEvent());
    }

    private Runnable makeWelcomeEvent() {
        return NarrativeEvent.inWorld(world)
                .withText("Hello! I am Frieda, your administrative assistant.\n\n" + makeListOfEmployeeNames() + " are currently maintaining our software. You can tap them to find out more about them.\n\nYou may reassign any number of them to new feature development at any time. Go ahead and try that now, and let me know when you are ready!")
                .addOption("OK").withAction(DO_NOTHING)
                .build();
    }

    private String makeListOfEmployeeNames() {
        final StringBuilder namesBuilder = new StringBuilder();
        final int numberOfWorkers = world.workers.size();
        for (int i = 0, limit = numberOfWorkers - 1; i < limit; i++) {
            final Entity e = world.workers.get(i);
            namesBuilder.append(world.name.get(e.id).shortName);
            namesBuilder.append(", ");
        }
        final Entity last = world.workers.get(numberOfWorkers - 1);
        namesBuilder.append("and ");
        namesBuilder.append(world.name.get(last.id).shortName);
        return namesBuilder.toString();
    }

}
