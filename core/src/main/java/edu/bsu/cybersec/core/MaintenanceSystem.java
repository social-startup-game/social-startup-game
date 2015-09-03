package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MaintenanceSystem extends tripleplay.entity.System {

    private final GameWorld world;

    public MaintenanceSystem(GameWorld world) {
        super(world, 0);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.maintenanceSkill) && entity.has(world.ownerId);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int id = entities.get(i);
            int companyId = world.ownerId.get(id);
            float change = clock.dt * world.maintenanceSkill.get(id);
            world.attackSurface.add(companyId, -change);
        }
    }
}
