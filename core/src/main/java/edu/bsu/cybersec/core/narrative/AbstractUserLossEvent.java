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

package edu.bsu.cybersec.core.narrative;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;

abstract class AbstractUserLossEvent extends NarrativeEvent {
    private final float percentLoss;

    /**
     * The number of users lost.
     * <p/>
     * This will be set in the {@link #run()} method so that it can be shown to the user
     * in the {@link #text()} method.
     */
    protected int loss;

    public AbstractUserLossEvent(GameWorld world, float percentLoss) {
        super(world);
        this.percentLoss = percentLoss;
    }

    @Override
    public void run() {
        final float initialNumberOfUsers = world.users.get();
        loss = (int) (initialNumberOfUsers * percentLoss);
        world.users.update(initialNumberOfUsers - loss);
        super.run();
    }
}
