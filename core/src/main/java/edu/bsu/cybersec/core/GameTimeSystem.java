package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

public final class GameTimeSystem extends tripleplay.entity.System {

    private final GameWorld world;

    public GameTimeSystem(GameWorld world) {
        super(world, 0);
        this.world = world;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.gameTime);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int id = entities.get(i);
            float scale = determineScale(id);
            int elapsedGameTime = (int) (clock.dt * scale);
            world.gameTime.add(entities.get(i), elapsedGameTime);
        }
    }

    private float determineScale(int id) {
        if (world.entity(id).has(world.gameTimeScale)) {
            return world.gameTimeScale.get(id);
        } else {
            return 1f;
        }
    }

}
