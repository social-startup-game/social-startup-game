package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ExpirySystem extends tripleplay.entity.System {
    private final GameWorld world;

    public ExpirySystem(GameWorld world) {
        super(world, 0);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.expiresIn);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int id = entities.get(i);
            final int oldRemaining = world.expiresIn.get(id);
            final int nowRemaining = oldRemaining - clock.dt;
            world.expiresIn.set(id, nowRemaining);
            if (nowRemaining <= 0) {
                world.entity(id).close();
            }
        }
    }
}
