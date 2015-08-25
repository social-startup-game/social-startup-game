package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

public final class TimeElapseSystem extends tripleplay.entity.System {

    private final GameWorld world;
    private float simSecondsPerClockSecond = 1f;
    protected final Entity simClockEntity;

    public TimeElapseSystem(GameWorld world) {
        super(world, 0);
        this.world = world;
        simClockEntity = createSimClock();
    }

    private Entity createSimClock() {
        Entity entity = world.create(true)
                .add(world.simClock);
        world.simClock.set(entity.id, new SimClock());
        return entity;
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
            simClock.advance(elapsedSimMs);
        }
    }

    public void setSimSecondsPerClockSecond(float simSecondsPerClockSecond) {
        this.simSecondsPerClockSecond = simSecondsPerClockSecond;
    }

    public void advance(int ms) {
        SimClock simClock = world.simClock.get(simClockEntity.id);
        simClock.advance(ms);
    }
}
