package edu.bsu.cybersec.core;

import tripleplay.entity.Component;
import tripleplay.entity.Entity;
import tripleplay.entity.World;

public abstract class GameWorld extends World {

    public final Component.Generic<SimClock> simClock = new Component.Generic<SimClock>(this);

    protected Entity createClockEntity() {
        Entity entity = create(true);
        entity.add(simClock);
        simClock.set(entity.id, new SimClock());
        return entity;
    }

    public static class Initialized extends GameWorld {
        public Initialized() {
            initializeTimeManagement();
        }

        private void initializeTimeManagement() {
            createClockEntity();
            new TimeElapseSystem(this);
        }
    }
}
