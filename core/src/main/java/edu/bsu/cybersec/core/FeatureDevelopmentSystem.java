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
        return isFeatureDevelopment(entity)
                || isDeveloper(entity);
    }

    private boolean isFeatureDevelopment(Entity entity) {
        return entity.has(world.featureId);
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
        distributeEffortEvenlyAcrossFeaturesInDevelopment(clock);
        processCompletedFeatures();
        clearBags();
    }

    private void triageEntitiesIntoBags(Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int entityId = entities.get(i);
            final Entity entity = world.entity(entityId);
            if (isFeatureDevelopment(entity)) {
                developmentBag.add(entityId);
            } else {
                developerBag.add(entityId);
            }
        }
    }

    private void distributeEffortEvenlyAcrossFeaturesInDevelopment(Clock clock) {
        float effortPerFeature = computeTotalDevelopmentEffort() / developmentBag.size();
        for (int i = 0, limit = developmentBag.size(); i < limit; i++) {
            int id = developmentBag.get(i);
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
        return world.progress.get(id) >= world.goal.get(id);
    }

    private void complete(int id) {
        final Entity developmentEntity = world.entity(id);
        developmentEntity.close();
        enableFeatureDevelopedBy(developmentEntity);
    }

    private void enableFeatureDevelopedBy(Entity developmentEntity) {
        final int featureEntityId = world.featureId.get(developmentEntity.id);
        final Entity featureEntity = world.entity(featureEntityId);
        featureEntity.setEnabled(true);
    }

    private void clearBags() {
        developmentBag.removeAll();
        developerBag.removeAll();
    }

}
