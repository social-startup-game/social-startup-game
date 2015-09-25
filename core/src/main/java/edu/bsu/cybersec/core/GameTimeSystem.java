package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.*;

public final class GameTimeSystem extends tripleplay.entity.System {

    private final GameWorld gameWorld;
    private float scale = 1f;

    public GameTimeSystem(GameWorld world) {
        super(world, SystemPriority.CLOCK_LEVEL.value);
        this.gameWorld = world;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return true;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        int elapsedGameTime = (int) (clock.dt * scale);
        gameWorld.advanceGameTime(elapsedGameTime);
    }

    public GameTimeSystem setScale(float scale) {
        checkArgument(scale > 0, "Scale must be positive");
        this.scale = scale;
        return this;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            forceElapsedTimeToBeZeroWhileDisabled();
        }
    }

    private void forceElapsedTimeToBeZeroWhileDisabled() {
        gameWorld.prevGameTimeMs = gameWorld.gameTimeMs;
    }
}
