package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;
import tripleplay.entity.IntBag;

import static com.google.common.base.Preconditions.checkNotNull;

public class FeatureDevelopmentSystem extends tripleplay.entity.System {

    private final GameWorld world;
    private final IntBag developmentBag = new IntBag();
    private final IntBag developerBag = new IntBag();

    public FeatureDevelopmentSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return isFeatureInDevelopment(entity)
                || isDeveloper(entity);
    }

    private boolean isFeatureInDevelopment(Entity entity) {
        return entity.has(world.developmentProgress);
    }

    private boolean isDeveloper(Entity entity) {
        return entity.has(world.developmentSkill)
                && entity.has(world.tasked)
                && world.tasked.get(entity.id) == Task.DEVELOPMENT;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        triageEntitiesIntoBags(entities);
        distributeEffortEvenlyAcrossFeaturesInDevelopment();
        processCompletedFeatures();
        clearBags();
    }

    private void triageEntitiesIntoBags(Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int entityId = entities.get(i);
            final Entity entity = world.entity(entityId);
            if (isFeatureInDevelopment(entity)) {
                developmentBag.add(entityId);
            } else {
                developerBag.add(entityId);
            }
        }
    }

    private void distributeEffortEvenlyAcrossFeaturesInDevelopment() {
        float dt = world.gameTimeMs - world.prevGameTimeMs;
        float effortPerFeature = computeTotalDevelopmentEffort() / developmentBag.size();
        for (int i = 0, limit = developmentBag.size(); i < limit; i++) {
            int id = developmentBag.get(i);
            float delta = effortPerFeature * dt / ClockUtils.MS_PER_HOUR;
            world.developmentProgress.add(id, delta);
        }
    }

    private float computeTotalDevelopmentEffort() {
        float effort = 0;
        for (int i = 0, limit = developerBag.size(); i < limit; i++) {
            int entityId = developerBag.get(i);
            if (isDeveloping(entityId)) {
                effort += world.developmentSkill.get(entityId);
            }
        }
        return effort;
    }

    private boolean isDeveloping(int entityId) {
        return world.tasked.get(entityId) == Task.DEVELOPMENT;
    }

    private void processCompletedFeatures() {
        for (int i = 0, limit = developmentBag.size(); i < limit; i++) {
            final int id = developmentBag.get(i);
            if (isComplete(id)) {
                complete(id);
            }
        }
    }

    private boolean isComplete(int id) {

        return world.entity(id).has(world.developmentProgress)
                && world.developmentProgress.get(id) >= world.goal.get(id);
    }

    private void complete(int id) {
        final Entity e = world.entity(id);
        e.remove(world.developmentProgress);
        world.vulnerabilityState.set(id, VulnerabilityState.ACTIVE.value);
    }

    private void clearBags() {
        developmentBag.removeAll();
        developerBag.removeAll();
    }

    public Entity makeFeatureEntity() {
        Entity entity = world.create(true)
                .add(world.developmentProgress,
                        world.goal,
                        world.usersPerSecond,
                        world.vulnerability,
                        world.vulnerabilityState);
        world.developmentProgress.set(entity.id, 0);
        world.usersPerSecond.set(entity.id, 0);
        world.vulnerabilityState.set(entity.id, VulnerabilityState.INACTIVE.value);
        return entity;
    }
}
