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

import static com.google.common.base.Preconditions.checkNotNull;

public final class TaskProgressSystem extends tripleplay.entity.System {

    private final GameWorld world;

    public TaskProgressSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.secondsRemaining);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int taskId = entities.get(i);
            final int ownerId = world.owner.get(taskId);
            if (world.task.get(ownerId) == taskId) {
                world.secondsRemaining.add(taskId, -world.gameTime.get().delta());
                handleCompletion(taskId);
            }
            if (world.entity(taskId).has(world.name)) {
                int hoursRemaining = world.secondsRemaining.get(taskId) / ClockUtils.SECONDS_PER_HOUR + 1;
                String newName = updateName(world.name.get(taskId), hoursRemaining);
                world.name.set(taskId, newName);
            }
        }
    }

    private void handleCompletion(final int taskId) {
        if (world.secondsRemaining.get(taskId) <= 0 && world.entity(taskId).has(world.onComplete)) {
            world.onComplete.get(taskId).run();
        }
    }

    static String updateName(String name, int hoursRemaining) {
        if (name.contains("(")) {
            return name.substring(0, name.indexOf('(') + 1) + hoursRemaining + "hr)";
        } else return name + " (" + hoursRemaining + "hr)";
    }
}
