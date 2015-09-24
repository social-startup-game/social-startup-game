package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class UpdatingSystem extends tripleplay.entity.System {

    private final GameWorld gameWorld;

    public UpdatingSystem(GameWorld world) {
        super(world, 0);
        this.gameWorld = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(gameWorld.onUpdate);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int id = entities.get(i);
            final Updatable updatable = gameWorld.onUpdate.get(id);
            updatable.update(clock);
        }
    }
}
