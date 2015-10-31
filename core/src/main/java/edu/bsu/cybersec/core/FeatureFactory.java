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

import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class FeatureFactory {

    private static final float DEFAULT_FEATURE_VULNERABILITY = 10f;
    private static final int DEFAULT_FEATURE_USERS_PER_HOUR = 50;

    public static FeatureBuilder in(GameWorld world) {
        return new FeatureBuilder(world);
    }

    public final static class FeatureBuilder {
        private final GameWorld world;

        private FeatureBuilder(GameWorld world) {
            this.world = checkNotNull(world);
        }

        public Entity makeFeatureInDevelopment(int featureNumber) {
            Entity entity = makeFeature(featureNumber);
            entity.add(world.developmentProgress,
                    world.goal);
            world.usersPerHourState.set(entity.id, UsersPerHourState.INACTIVE.value);
            return entity;
        }

        private Entity makeFeature(int featureNumber) {
            Entity entity = world.create(true)
                    .add(world.featureNumber,
                            world.name,
                            world.usersPerHour,
                            world.usersPerHourState,
                            world.vulnerability);
            world.featureNumber.set(entity.id, featureNumber);
            world.name.set(entity.id, Name.simply("Unnamed feature"));
            world.vulnerability.set(entity.id, DEFAULT_FEATURE_VULNERABILITY);
            world.usersPerHour.set(entity.id, DEFAULT_FEATURE_USERS_PER_HOUR);
            return entity;
        }

        public Entity makeCompletedFeature(int featureNumber) {
            Entity entity = makeFeature(featureNumber);
            world.usersPerHourState.set(entity.id, UsersPerHourState.ACTIVE.value);
            return entity;
        }
    }


}
