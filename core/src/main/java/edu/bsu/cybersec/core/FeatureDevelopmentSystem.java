package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;
import tripleplay.entity.IntBag;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class FeatureDevelopmentSystem extends tripleplay.entity.System {

    private final GameWorld world;
    private final IntBag featureBag = new IntBag();
    private final IntBag developerBag = new IntBag();

    public FeatureDevelopmentSystem(GameWorld world) {
        super(world, 0);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return isFeature(entity)
                || isDeveloper(entity);
    }

    private boolean isFeature(Entity entity) {
        return entity.has(world.type)
                && world.type.get(entity.id) == Type.FEATURE_IN_DEVELOPMENT;
    }

    private boolean isDeveloper(Entity entity) {
        return entity.has(world.developmentSkill)
                && entity.has(world.tasked)
                && world.tasked.get(entity.id).equals(Task.DEVELOPMENT);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        triageEntitiesIntoBags(entities);
        distributeEffortEvenlyAcrossFeaturesInDevelopment(clock);
        processCompletedFeatures();
        clearBags();
    }

    private void triageEntitiesIntoBags(Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int entityId = entities.get(i);
            final Entity entity = world.entity(entityId);
            if (isFeature(entity)) {
                featureBag.add(entityId);
            } else {
                developerBag.add(entityId);
            }
        }
    }

    private void distributeEffortEvenlyAcrossFeaturesInDevelopment(Clock clock) {
        float effortPerFeature = computeTotalDevelopmentEffort() / featureBag.size();
        for (int i = 0, limit = featureBag.size(); i < limit; i++) {
            int id = featureBag.get(i);
            float delta = effortPerFeature * clock.dt / 1000;
            world.progress.add(id, delta);
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
        return world.tasked.get(entityId).equals(Task.DEVELOPMENT);
    }

    private void processCompletedFeatures() {
        for (int i = 0, limit = featureBag.size(); i < limit; i++) {
            final int id = featureBag.get(i);
            if (isComplete(id)) {
                complete(id);
            }
        }
    }

    private boolean isComplete(int id) {
        return world.progress.get(id) >= world.goal.get(id);
    }

    private void complete(int id) {
        checkState(world.type.get(id) == Type.FEATURE_IN_DEVELOPMENT);
        final Entity e = world.entity(id);
        e.remove(world.progress)
                .remove(world.goal);
        e.add(world.usersPerSecond);
        world.usersPerSecond.set(id, 20);
        world.type.set(id, Type.FEATURE_COMPLETE);
        world.entity(id).didChange();
    }


    private void clearBags() {
        featureBag.removeAll();
        developerBag.removeAll();
    }

}
