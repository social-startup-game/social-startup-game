package edu.bsu.cybersec.core;

import tripleplay.entity.Component;
import tripleplay.entity.Entity;
import tripleplay.entity.World;

public abstract class GameWorld extends World {

    public final Component.IScalar elapsedSimMs = new Component.IScalar(this);
    public final Component.IScalar tickMs = new Component.IScalar(this);

    protected Entity createClockEntity() {
        Entity entity = create(true);
        entity.add(elapsedSimMs, tickMs);
        elapsedSimMs.set(entity.id, 0);
        tickMs.set(entity.id, 0);
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
