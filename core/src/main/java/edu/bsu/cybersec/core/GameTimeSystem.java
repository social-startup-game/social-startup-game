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

import static com.google.common.base.Preconditions.checkArgument;

public final class GameTimeSystem extends tripleplay.entity.System {

    private final GameWorld gameWorld;
    private float scale = 1f;

    public GameTimeSystem(GameWorld world) {
        super(world, SystemPriority.CLOCK_LEVEL.value);
        this.gameWorld = world;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return true;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        int elapsedGameTime = (int) (clock.dt * scale);
        gameWorld.advanceGameTime(elapsedGameTime);
    }

    public GameTimeSystem setScale(float scale) {
        checkArgument(scale > 0, "Scale must be positive");
        this.scale = scale;
        return this;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            forceElapsedTimeToBeZeroWhileDisabled();
        }
    }

    private void forceElapsedTimeToBeZeroWhileDisabled() {
        gameWorld.prevGameTimeMs = gameWorld.gameTimeMs;
    }
}
