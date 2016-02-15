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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import tripleplay.entity.Entity;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class FeatureFactory {

    private static final String[] DEFAULT_FEATURE_NAMES = {
            "Selfie of the Week",
            "Tumblr Pun",
            "Twitter Nest",
            "Hollywood Ville",
            "Fun Ville",
            "Puppy Ville",
            "Cat Ville",
            "Village Ville",
            "Dislike Button",
            "User reviews"
    };

    private static final RandomInRange vulnerabilityRandomizer = new RandomInRange(1, 4);

    public static FeatureFactory in(GameWorld world) {
        return new FeatureFactory(world);
    }

    private final GameWorld world;
    private List<String> namesToDrawFrom;
    private List<String> nextNameQueue;

    private FeatureFactory(GameWorld world) {
        this.world = checkNotNull(world);
        withNames(Lists.newArrayList(DEFAULT_FEATURE_NAMES));
    }

    public FeatureFactory withNames(Iterable<String> names) {
        this.namesToDrawFrom = ImmutableList.copyOf(names);
        this.nextNameQueue = Lists.newArrayList(namesToDrawFrom);
        Shuffler.shuffle(nextNameQueue);
        return this;
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
        world.name.set(entity.id, nextFeatureName());
        world.vulnerability.set(entity.id, vulnerabilityRandomizer.nextInt() * 0.01f);
        world.usersPerHour.set(entity.id, generateUsersPerHour());
        return entity;
    }

    private String nextFeatureName() {
        if (nextNameQueue.isEmpty()) {
            withNames(namesToDrawFrom);
        }
        return nextNameQueue.remove(0);
    }

    private float generateUsersPerHour() {
        int min = 50;
        int max = 200;
        int range = max - min;
        return (float) Math.floor(Math.random() * range) + min;
    }

    public Entity makeCompletedFeature(int featureNumber) {
        Entity entity = makeFeature(featureNumber);
        world.usersPerHourState.set(entity.id, UsersPerHourState.ACTIVE.value);
        return entity;
    }
}
