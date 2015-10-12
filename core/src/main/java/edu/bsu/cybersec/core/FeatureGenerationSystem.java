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
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class FeatureGenerationSystem extends tripleplay.entity.System {
    private final GameWorld world;
    private int nextFeatureNumber;

    public FeatureGenerationSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = checkNotNull(world);
    }

    public FeatureGenerationSystem nextFeatureNumber(int number) {
        this.nextFeatureNumber = number;
        return this;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.developmentProgress);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        if (entities.size() == 0) {
            final Entity e = FeatureFactory.in(world).makeFeatureInDevelopment(nextFeatureNumber++);
            world.goal.set(e.id, 20);
        }
    }
}
