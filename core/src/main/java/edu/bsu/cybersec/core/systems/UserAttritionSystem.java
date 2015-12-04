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

package edu.bsu.cybersec.core.systems;

import edu.bsu.cybersec.core.ClockUtils;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import tripleplay.entity.Entity;
import tripleplay.entity.System;

import static com.google.common.base.Preconditions.*;

public final class UserAttritionSystem extends System {

    private final GameWorld world;

    public UserAttritionSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.userAttrition);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int id = entities.get(i);
            float attrition = world.userAttrition.get(id) * world.gameTime.get().delta() / ClockUtils.SECONDS_PER_HOUR;
            float initialUsers = world.users.get();
            float lostUsers = initialUsers * attrition;
            float totalUsers = Math.max(0, initialUsers - lostUsers);
            world.lostUsers.add(id, lostUsers);
            world.users.update(totalUsers);
        }
    }
}
