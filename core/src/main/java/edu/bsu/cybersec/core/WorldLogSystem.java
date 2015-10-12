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

import playn.core.Clock;
import tripleplay.entity.Component;
import tripleplay.entity.Entity;

public final class WorldLogSystem extends tripleplay.entity.System {

    private final GameWorld gameWorld;

    public WorldLogSystem(GameWorld gameWorld) {
        super(gameWorld, SystemPriority.DEBUG_LEVEL.value);
        this.gameWorld = gameWorld;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return true;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        final int limit = entities.size();
        for (String name : gameWorld.components.keySet()) {
            Component component = gameWorld.components.get(name);
            int count = 0;
            for (int i = 0; i < limit; i++) {
                if (gameWorld.entity(entities.get(i)).has(component)) {
                    count++;
                }
            }
            debug(name + ": " + count);
        }
        setEnabled(false);
    }

    private static void debug(String mesg) {
        SimGame.game.plat.log().debug(mesg);
    }


}
