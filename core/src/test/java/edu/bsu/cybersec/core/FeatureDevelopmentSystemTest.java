package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureDevelopmentSystemTest extends AbstractSystemTest {

    @Override
    public void setUp() {
        super.setUp();
        new FeatureDevelopmentSystem(world);
    }

    @Test
    public void testUpdate_noDevelopers_noProgress() {
        Entity featureEntity = makeFeatureInDevelopment();
        advanceOneDay();
        assertEquals(0, world.progress.get(featureEntity.id), EPSILON);
    }

    private Entity makeFeatureInDevelopment() {
        Entity entity = world.create(true)
                .add(world.type, world.progress);
        world.type.set(entity.id, Type.FEATURE);
        world.progress.set(entity.id, 0);
        return entity;
    }

    @Test
    public void testUpdate_oneTaskedEntityIdle_oneFeatureToDevelop_noProgress() {
        createIdleDeveloper();
        Entity inDevelopment = makeFeatureInDevelopment();
        advanceOneDay();
        assertEquals(0, world.progress.get(inDevelopment.id), EPSILON);
    }

    private void createIdleDeveloper() {
        Entity developer = createTasklessDeveloper(1);
        world.tasked.set(developer.id, Task.IDLE);
    }

    private Entity createTasklessDeveloper(float rate) {
        Entity developer = world.create(true).add(world.tasked, world.developmentSkill);
        world.developmentSkill.set(developer.id, (int) rate);
        return developer;
    }

    private void createActiveDeveloper(float rate) {
        Entity developer = createTasklessDeveloper(rate);
        world.tasked.set(developer.id, Task.DEVELOPMENT);
    }

    @Test
    public void testPositiveProgressRate_positiveProgress() {
        Entity entity = makeFeatureInDevelopment();
        createActiveDeveloper(10);
        advanceOneDay();
        assertTrue(world.progress.get(entity.id) > EPSILON);
    }

    @Test
    public void testUpdate_defaultProgressUnitsArePerSecond() {
        final float amountPerSecond = 10;
        Entity entity = makeFeatureInDevelopment();
        createActiveDeveloper(10);
        advanceOneSecond();
        assertEquals(amountPerSecond, world.progress.get(entity.id), EPSILON);
    }
}
