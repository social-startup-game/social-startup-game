package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

public class TimeElapseSystem extends tripleplay.entity.System {

    private final GameWorld world;

    public TimeElapseSystem(GameWorld world) {
        super(world, 0);
        this.world = world;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.elapsedSimMs);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int entityId = entities.get(i);
            world.elapsedSimMs.set(entityId, clock.dt);
        }
    }
}
