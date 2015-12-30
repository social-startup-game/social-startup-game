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
import edu.bsu.cybersec.core.TaskFlags;
import playn.core.Clock;
import tripleplay.entity.Component;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class LearningSystem extends tripleplay.entity.System {

    public static final float SKILL_PER_GAME_HOUR = 0.1f;

    private final GameWorld world;
    private float elapsedHours;

    public LearningSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.task) && entity.has(world.developmentSkill);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        this.elapsedHours = (float) (world.gameTime.get().delta()) / ClockUtils.SECONDS_PER_HOUR;

        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int id = entities.get(i);
            updateSkills(id);
        }
    }

    private void updateSkills(final int id) {
        final int taskId = world.task.get(id);
        final int taskFlags = world.taskFlags.get(taskId);
        Component.FScalar c = determineRelevantSkill(taskFlags);
        if (c != null) {
            float start = c.get(id);
            float updated = start + elapsedHours * SKILL_PER_GAME_HOUR;
            c.set(id, updated);
        }
    }

    private Component.FScalar determineRelevantSkill(int taskFlags) {
        if (TaskFlags.DEVELOPMENT.isSet(taskFlags)) {
            return world.developmentSkill;
        } else if (TaskFlags.MAINTENANCE.isSet(taskFlags)) {
            return world.maintenanceSkill;
        } else {
            return null;
        }
    }

}
