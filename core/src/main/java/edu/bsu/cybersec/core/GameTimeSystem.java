package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkArgument;

public final class GameTimeSystem extends tripleplay.entity.System {

    private final GameWorld gameWorld;
    private float scale = 1f;

    public GameTimeSystem(GameWorld world) {
        super(world, 0);
        this.gameWorld = world;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return false;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int id = entities.get(i);
            float scale = determineScale(id);
            int elapsedGameTime = (int) (clock.dt * scale);
            gameWorld.gameTimeMs += elapsedGameTime;
        }
    }

    private float determineScale(int id) {
        return 1f;
    }

    public GameTimeSystem setScale(float scale) {
        checkArgument(scale > 0, "Scale must be positive");
        this.scale = scale;
        return this;
    }
}
