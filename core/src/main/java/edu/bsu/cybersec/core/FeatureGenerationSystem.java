package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class FeatureGenerationSystem extends tripleplay.entity.System {
    private final GameWorld world;
    private int nextFeatureNumber;

    public FeatureGenerationSystem(GameWorld world) {
        super(world, SystemPriority.MODEL_LEVEL.value);
        this.world = checkNotNull(world);
    }

    public FeatureGenerationSystem nextFeatureNumber(int number) {
        this.nextFeatureNumber = number;
        return this;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.developmentProgress);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        if (entities.size() == 0) {
            final Entity e = FeatureFactory.in(world).makeFeatureInDevelopment(nextFeatureNumber++);
            world.goal.set(e.id, 20);
        }
    }
}
