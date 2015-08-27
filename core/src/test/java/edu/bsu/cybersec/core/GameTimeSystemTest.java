package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public final class GameTimeSystemTest extends AbstractSystemTest {

    private Entity gameClock;

    @Override
    public void setUp() {
        super.setUp();
        new GameTimeSystem(world);
        createGameTimeEntity();
    }

    private void createGameTimeEntity() {
        Entity entity = world.create(true).add(world.type, world.gameTime);
        world.type.set(entity.id, Type.CLOCK);
        world.gameTime.set(entity.id, 0);
        gameClock = entity;
    }

    @Test
    public void testScale_oneMs() {
        gameClock.add(world.gameTimeScale);
        world.gameTimeScale.set(gameClock.id, 10);
        advanceOneMillisecond();
        assertEquals(10, currentGameTime());
    }

    private int currentGameTime() {
        return world.gameTime.get(gameClock.id);
    }

}
