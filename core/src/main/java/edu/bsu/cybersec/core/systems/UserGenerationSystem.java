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
import edu.bsu.cybersec.core.UsersPerHourState;
import playn.core.Clock;
import tripleplay.entity.Entity;

public class UserGenerationSystem extends tripleplay.entity.System {
    private final GameWorld world;

    public UserGenerationSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = world;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        final float delta = world.gameTime.get().delta();
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int id = entities.get(i);
            float usersPerHour = world.usersPerHour.get(id);
            float additionalUsers = usersPerHour * delta / ClockUtils.SECONDS_PER_HOUR;
            addUsers(additionalUsers);
        }
    }

    private void addUsers(float additionalUsers) {
        world.users.update(world.users.get() + additionalUsers);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return isActiveUserGeneratingEntity(entity);
    }

    public boolean isActiveUserGeneratingEntity(Entity entity) {
        return entity.has(world.usersPerHour)
                && world.usersPerHourState.get(entity.id) == UsersPerHourState.ACTIVE.value;
    }
}