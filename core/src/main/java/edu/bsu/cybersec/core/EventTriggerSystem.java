package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class EventTriggerSystem extends tripleplay.entity.System {
    private final GameWorld gameWorld;

    public EventTriggerSystem(GameWorld gameWorld) {
        super(gameWorld, 0);
        this.gameWorld = checkNotNull(gameWorld);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        boolean interested = entity.has(gameWorld.timeTrigger);
        checkState(entity.has(gameWorld.event), "I expect every time triggerable thing to have an event.");
        return interested;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            final int id = entities.get(i);
            final int time = gameWorld.timeTrigger.get(id);
            if (time > gameWorld.prevGameTimeMs && time <= gameWorld.gameTimeMs) {
                gameWorld.event.get(id).run();
            }
        }
    }
}
