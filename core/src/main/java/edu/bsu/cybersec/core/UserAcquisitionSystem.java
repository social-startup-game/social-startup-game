package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

public class UserAcquisitionSystem extends tripleplay.entity.System {
    private final GameWorld world;
    private int elapsedMs = 0;

    public UserAcquisitionSystem(GameWorld world) {
        super(world, 0);
        this.world = world;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int entityId = entities.get(i);
            updateEntity(clock.dt, entityId);
        }
    }

    private void updateEntity(int dt, int entityId) {
        Feature feature = world.feature.get(entityId);
        feature.msUtilUserAcquisition -= dt;
        while (feature.msUtilUserAcquisition <= 0) {
            Company company = world.company.get(feature.companyId);
            company.users++;
            feature.msUtilUserAcquisition += computeMsUntilUserAcquisition(feature.usersPerDay);
        }
    }

    private int computeMsUntilUserAcquisition(float usersPerDay) {
        return (int) (ClockUtils.MS_PER_DAY / usersPerDay);
    }

    @Override
    protected void wasAdded(Entity entity) {
        super.wasAdded(entity);
        Feature feature = world.feature.get(entity.id);
        if (feature.usersPerDay != 0) {
            feature.msUtilUserAcquisition = computeMsUntilUserAcquisition(feature.usersPerDay);
        }
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.feature);
    }
}