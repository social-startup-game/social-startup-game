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
                .add(world.type, world.progress, world.progressRate);
        world.type.set(entity.id, Type.FEATURE);
        world.progress.set(entity.id, 0);
        world.progressRate.set(entity.id, 0);
        return entity;
    }

    @Test
    public void testUpdate_oneTaskedEntityIdle_oneFeatureToDevelop_noProgress() {
        createIdleDeveloper();
        Entity inDevelopment = makeFeatureInDevelopment();
        advanceOneDay();
        assertEquals(0, world.progress.get(inDevelopment.id), EPSILON);
    }

    @Test
    public void testUpdate_oneEntityDeveloping_oneFeatureToDevelop_developRateIsPositive() {
        createActiveDeveloper();
        Entity inDevelopment = makeFeatureInDevelopment();
        advanceOneDay();
        assertTrue(world.progressRate.get(inDevelopment.id) > 0);
    }

    private void createIdleDeveloper() {
        Entity developer = createTasklessDeveloper();
        world.tasked.set(developer.id, Task.IDLE);
    }

    private Entity createTasklessDeveloper() {
        Entity developer = world.create(true).add(world.tasked, world.developmentSkill);
        world.developmentSkill.set(developer.id, 1);
        return developer;
    }

    private void createActiveDeveloper() {
        Entity developer = createTasklessDeveloper();
        world.tasked.set(developer.id, Task.DEVELOPMENT);
    }
}
