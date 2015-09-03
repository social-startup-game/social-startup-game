package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class MaintenanceSystem extends tripleplay.entity.System {

    private final GameWorld world;

    public MaintenanceSystem(GameWorld world) {
        super(world, 0);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        boolean interested = entity.has(world.tasked)
                && world.tasked.get(entity.id) == Task.MAINTENANCE;
        if (interested) {
            checkState(entity.has(world.maintenanceSkill));
            checkState(entity.has(world.companyId));
        }
        return interested;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int id = entities.get(i);
            int companyId = world.companyId.get(id);
            float change = clock.dt * world.maintenanceSkill.get(id);
            world.attackSurface.add(companyId, -change);
        }
    }
}
