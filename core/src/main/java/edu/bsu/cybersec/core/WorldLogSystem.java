package edu.bsu.cybersec.core;

import playn.core.Clock;
import tripleplay.entity.Component;
import tripleplay.entity.Entity;

public final class WorldLogSystem extends tripleplay.entity.System {

    private final GameWorld gameWorld;

    public WorldLogSystem(GameWorld gameWorld) {
        super(gameWorld, SystemPriority.DEBUG_LEVEL.value);
        this.gameWorld = gameWorld;
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return true;
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        final int limit = entities.size();
        for (String name : gameWorld.components.keySet()) {
            Component component = gameWorld.components.get(name);
            int count = 0;
            for (int i = 0; i < limit; i++) {
                if (gameWorld.entity(entities.get(i)).has(component)) {
                    count++;
                }
            }
            debug(name + ": " + count);
        }
        setEnabled(false);
    }

    private static void debug(String mesg) {
        SimGame.game.plat.log().debug(mesg);
    }


}
