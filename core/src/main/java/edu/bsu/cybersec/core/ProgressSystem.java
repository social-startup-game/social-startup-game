package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProgressSystem extends tripleplay.entity.System {

    private enum Unit {
        PER_SECOND(ClockUtils.MS_PER_SECOND);
        private int value;

        Unit(int value) {
            this.value = value;
        }
    }

    private Unit unit = Unit.PER_SECOND;
    private final GameWorld world;

    public ProgressSystem(GameWorld world) {
        super(world, 0);
        this.world = checkNotNull(world);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.progress) && entity.has(world.progressRate);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int id = entities.get(i);
            float progress = world.progress.get(id);
            float progressRate = world.progressRate.get(id);
            float newProgress = progress + progressRate * clock.dt / unit.value;
            world.progress.set(id, newProgress);
        }
    }
}
