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
import edu.bsu.cybersec.core.GameTime;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import react.Value;
import tripleplay.entity.Entity;

public class GameTimeSystem extends tripleplay.entity.System {

    private static final float DEFAULT_SCALE = ClockUtils.SECONDS_PER_HOUR * 2;
    private final GameWorld gameWorld;
    private final Value<Float> scale = Value.create(DEFAULT_SCALE);

    /**
     * Indicate if this system is enabled.
     * <p/>
     * The superclass has a <code>_enabled</code> field that represents this information, but it is private and, hence,
     * inaccessible here. However, other parts of the game need to know if this system is enabled or not, and so
     * we duplicate that data here to be able to use the {@link #isEnabled()} accessor.
     */
    private boolean enabled = true;

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
        int elapsedGameTime = (int) (clock.dt * scale.get() / ClockUtils.MS_PER_SECOND);
        gameWorld.advanceGameTime(elapsedGameTime);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
        if (!enabled) {
            forceElapsedTimeToBeZeroWhileDisabled();
        }
    }

    private void forceElapsedTimeToBeZeroWhileDisabled() {
        GameTime t = gameWorld.gameTime.get();
        if (t.previous != t.now) {
            gameWorld.gameTime.update(new GameTime(t.now, t.now));
        }
    }

    public Value<Float> scale() {
        return scale;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
