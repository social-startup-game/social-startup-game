package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.*;

public class FeatureDevelopmentSystemTest extends AbstractSystemTest {

    private Entity completedFeatureEntity;
    private Entity company;

    @Override
    public void setUp() {
        super.setUp();
        new FeatureDevelopmentSystem(world);
        completedFeatureEntity = null;
        company = world.create(true);
    }

    @Test
    public void testUpdate_noDevelopers_noProgress() {
        Entity featureEntity = makeFeatureInDevelopment();
        advanceOneDay();
        assertEquals(0, world.progress.get(featureEntity.id), EPSILON);
    }

    private Entity makeFeatureInDevelopment() {
        Entity entity = world.create(true)
                .add(world.type, world.progress, world.goal, world.owner);
        world.type.set(entity.id, Type.FEATURE_IN_DEVELOPMENT);
        world.progress.set(entity.id, 0);
        world.goal.set(entity.id, 100);
        world.owner.set(entity.id, company.id);
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

    @Test
    public void testFeatureCompletion_progressComponentRemoved() {
        whenAFeatureIsCompleted();
        assertFalse(completedFeatureEntity.has(world.progress));
    }

    private void whenAFeatureIsCompleted() {
        completedFeatureEntity = makeFeatureInDevelopmentRequiring(0);
        createActiveDeveloper(10);
        advanceOneSecond();
    }

    private Entity makeFeatureInDevelopmentRequiring(int goal) {
        Entity e = makeFeatureInDevelopment();
        e.add(world.goal);
        world.goal.set(e.id, goal);
        return e;
    }

    @Test
    public void testFeatureCompletion_goalComponentRemoved() {
        whenAFeatureIsCompleted();
        assertFalse(completedFeatureEntity.has(world.goal));
    }

    @Test
    public void testFeatureCompletion_changesEntityType() {
        whenAFeatureIsCompleted();
        assertEquals(Type.FEATURE_COMPLETE, world.type.get(completedFeatureEntity.id));
    }

    @Test
    public void testFeatureCompletion_enablesUserGeneration() {
        whenAFeatureIsCompleted();
        assertTrue(completedFeatureEntity.has(world.usersPerSecond));
    }

    @Test
    public void testFeatureCompletion_featureHasExposure() {
        whenAFeatureIsCompleted();
        assertTrue(completedFeatureEntity.has(world.exposure));
    }

    @Test
    public void testFeatureCompletion_featureHasAnOwner() {
        whenAFeatureIsCompleted();
        assertTrue(completedFeatureEntity.has(world.owner));
    }
}
