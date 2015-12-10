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

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import playn.scene.Layer;
import pythagoras.f.Point;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class LayerPositionSystem extends tripleplay.entity.System {
    private final GameWorld world;
    private final Point pointCache = new Point();

    public LayerPositionSystem(GameWorld world) {
        super(world, SystemPriority.UI_LEVEL.value);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.sprite) && entity.has(world.position);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int id = entities.get(i);
            Layer layer = world.sprite.get(id);
            world.position.get(id, pointCache);
            layer.setTranslation(pointCache);
        }
    }
}
