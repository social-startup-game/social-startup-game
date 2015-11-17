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

package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public final class FeatureGroup extends ScrollingListInteractionAreaGroup {

    private final GameWorld world;

    public FeatureGroup(final GameWorld gameWorld) {
        super(gameWorld, gameWorld.featureNumber);
        this.world = checkNotNull(gameWorld);
    }

    @Override
    protected ScrollingListItem createLabel(int entityId) {
        return new FeatureLabel(entityId);
    }

    private final class FeatureLabel extends ScrollingListItem {

        private boolean hasCompletedText = false;

        FeatureLabel(int entityId) {
            super(entityId);
        }

        @Override
        public void update(int entityId) {
            Entity entity = world.entity(entityId);
            final int number = world.featureNumber.get(entityId);
            final String numberAndName = number + ": " + world.name.get(entityId);
            if (entity.has(world.developmentProgress)) {
                final int progress = (int) (world.developmentProgress.get(entityId) / world.goal.get(entityId) * 100);
                text.update(numberAndName + " [" + progress + "%]");
            } else if (!hasCompletedText) {
                final float usersPerHour = world.usersPerHour.get(entityId);
                if (number % 10 == 0) {
                    text.update(numberAndName + " - " + (int) usersPerHour + "  Users Per Hour");
                } else {
                    text.update(numberAndName + " - " + (int) usersPerHour);
                }
                hasCompletedText = true;
            }
        }
    }
}
