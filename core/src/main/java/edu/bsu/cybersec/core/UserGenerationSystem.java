package edu.bsu.cybersec.core;

import playn.core.Clock;
import react.Value;
import tripleplay.entity.Entity;

public class UserGenerationSystem extends tripleplay.entity.System {
    private final GameWorld world;
    private int elapsedMs = 0;
    public Value<Float> users = Value.create(0f);

    public UserGenerationSystem(GameWorld world) {
        super(world, 0);
        this.world = world;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int id = entities.get(i);
            float usersPerSecond = world.usersPerSecond.get(id);
            float additionalUsers = usersPerSecond * clock.dt / 1000;
            users.update(users.get() + additionalUsers);
        }
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.usersPerSecond);
    }
}