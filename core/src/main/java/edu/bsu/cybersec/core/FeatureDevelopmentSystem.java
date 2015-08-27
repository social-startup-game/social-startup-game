package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;
import tripleplay.entity.IntBag;

import static com.google.common.base.Preconditions.checkNotNull;

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
                && world.type.get(entity.id) == Type.FEATURE
                && entity.has(world.progressRate);
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
        distributeEffortEvenlyAcrossFeaturesInDevelopment();
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

    private void distributeEffortEvenlyAcrossFeaturesInDevelopment() {
        float effortPerFeature = computeTotalDevelopmentEffort() / featureBag.size();
        for (int i = 0, limit = featureBag.size(); i < limit; i++) {
            int entityId = featureBag.get(i);
            world.progressRate.set(entityId, effortPerFeature);
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

    private void clearBags() {
        featureBag.removeAll();
        developerBag.removeAll();
    }

}
