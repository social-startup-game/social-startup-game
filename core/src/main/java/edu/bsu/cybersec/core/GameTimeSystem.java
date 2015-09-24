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
        configureClockUpdates();
    }

    private void configureClockUpdates() {
        Entity e = gameWorld.create(true)
                .add(gameWorld.onUpdate);
        gameWorld.onUpdate.set(e.id, new Updatable() {
            @Override
            public void update(Clock clock) {
                int elapsedGameTime = (int) (clock.dt * scale);
                gameWorld.advanceGameTime(elapsedGameTime);
            }
        });
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return false;
    }

    public GameTimeSystem setScale(float scale) {
        checkArgument(scale > 0, "Scale must be positive");
        this.scale = scale;
        return this;
    }
}
