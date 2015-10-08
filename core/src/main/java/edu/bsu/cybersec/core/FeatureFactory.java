package edu.bsu.cybersec.core;

import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class FeatureFactory {

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
            world.vulnerabilityState.set(entity.id, VulnerabilityState.INACTIVE.value);
            world.usersPerHourState.set(entity.id, UsersPerHourState.INACTIVE.value);
            return entity;
        }

        private Entity makeFeature(int featureNumber) {
            Entity entity = world.create(true)
                    .add(world.featureNumber,
                            world.name,
                            world.usersPerHour,
                            world.usersPerHourState,
                            world.vulnerability,
                            world.vulnerabilityState);
            world.featureNumber.set(entity.id, featureNumber);
            world.name.set(entity.id, "Unnamed feature");
            world.vulnerability.set(entity.id, 10);
            return entity;
        }

        public Entity makeCompletedFeature(int featureNumber) {
            Entity entity = makeFeature(featureNumber);
            world.vulnerabilityState.set(entity.id, VulnerabilityState.ACTIVE.value);
            world.usersPerHourState.set(entity.id, UsersPerHourState.ACTIVE.value);
            return entity;
        }
    }


}
