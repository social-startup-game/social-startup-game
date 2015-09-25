package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

public class UserGenerationSystem extends tripleplay.entity.System {
    private final GameWorld world;

    public UserGenerationSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = world;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        float delta = world.gameTimeMs - world.prevGameTimeMs;
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int id = entities.get(i);
            float usersPerSecond = world.usersPerSecond.get(id);
            float additionalUsers = usersPerSecond * delta / 1000;
            int ownerId = world.companyId.get(id);
            world.users.add(ownerId, additionalUsers);
        }
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.usersPerSecond) && entity.has(world.companyId);
    }
}