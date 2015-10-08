package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class MaintenanceSystem extends tripleplay.entity.System {

    private final GameWorld world;

    public MaintenanceSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        boolean interested = entity.has(world.tasked)
                && world.tasked.get(entity.id) == Task.MAINTENANCE;
        if (interested) {
            checkState(entity.has(world.maintenanceSkill));
        }
        return interested;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int id = entities.get(i);
            float currentExposure = world.exposure.get();
            float change = currentExposure * world.maintenanceSkill.get(id) * clock.dt / 1000;
            world.exposure.update(currentExposure - change);
        }
    }
}
