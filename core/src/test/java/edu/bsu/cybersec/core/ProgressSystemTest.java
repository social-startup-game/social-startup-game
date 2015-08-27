package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProgressSystemTest extends AbstractSystemTest {

    @Override
    public void setUp() {
        super.setUp();
        new ProgressSystem(world);
    }

    @Test
    public void testZeroProgressRate_noProgress() {
        Entity entity = createDefaultProgressEntity();
        advanceOneDay();
        assertEquals(0, world.progress.get(entity.id), EPSILON);
    }

    private Entity createDefaultProgressEntity() {
        Entity entity = world.create(true).add(world.progressRate, world.progress, world.goal);
        world.progress.set(entity.id, 0);
        world.goal.set(entity.id, 0);
        world.progressRate.set(entity.id, 0);
        return entity;
    }

    @Test
    public void testPositiveProgressRate_positiveProgress() {
        Entity entity = createDefaultProgressEntity();
        world.progressRate.set(entity.id, 10);
        advanceOneDay();
        assertTrue(world.progress.get(entity.id) > EPSILON);
    }

    @Test
    public void testUpdate_defaultProgressUnitsArePerSecond() {
        final float amountPerSecond = 10;
        Entity entity = createDefaultProgressEntity();
        world.progressRate.set(entity.id, amountPerSecond);
        advanceOneSecond();
        assertEquals(amountPerSecond, world.progress.get(entity.id), EPSILON);
    }

}
