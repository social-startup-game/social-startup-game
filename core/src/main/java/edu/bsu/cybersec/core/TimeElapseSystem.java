package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

public final class TimeElapseSystem extends tripleplay.entity.System {

    private final GameWorld world;
    private float simSecondsPerClockSecond = 1f;

    public TimeElapseSystem(GameWorld world) {
        super(world, 0);
        this.world = world;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.simClock);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int entityId = entities.get(i);
            int elapsedSimMs = (int) (clock.dt * simSecondsPerClockSecond);
            SimClock simClock = world.simClock.get(entityId);
            simClock.elapsedMS = elapsedSimMs;
            simClock.tickMS += elapsedSimMs;
        }
    }

    public void setSimSecondsPerClockSecond(float simSecondsPerClockSecond) {
        this.simSecondsPerClockSecond = simSecondsPerClockSecond;
    }
}
