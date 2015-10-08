package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureDevelopmentSystemTest extends AbstractSystemTest {

    private Entity completedDevelopmentEntity;

    @Override
    public void setUp() {
        super.setUp();
        new FeatureDevelopmentSystem(world);
        completedDevelopmentEntity = null;
    }

    @Test
    public void testUpdate_noDevelopers_noProgress() {
        Entity developmentEntity = makeFeatureInDevelopmentAndReturnDevelopmentEntity();
        advanceOneDay();
        thenThereIsNoProgressOn(developmentEntity);
    }

    private Entity makeFeatureInDevelopmentAndReturnDevelopmentEntity() {
        Entity featureEntity = createDisabledFeatureEntity();
        return createDevelopmentEntityForFeature(featureEntity);
    }

    private Entity createDisabledFeatureEntity() {
        Entity featureEntity = world.create(false)
                .add(world.usersPerSecond, world.companyId);
        world.usersPerSecond.set(featureEntity.id, 20);
        return featureEntity;
    }

    private Entity createDevelopmentEntityForFeature(Entity featureEntity) {
        Entity developmentEntity = world.create(true)
                .add(world.developmentProgress, world.goal, world.featureId);
        world.developmentProgress.set(developmentEntity.id, 0);
        world.goal.set(developmentEntity.id, 100);
        world.featureId.set(developmentEntity.id, featureEntity.id);
        return developmentEntity;
    }

    private void thenThereIsNoProgressOn(Entity developmentEntity) {
        assertEquals(0, world.developmentProgress.get(developmentEntity.id), EPSILON);
    }

    @Test
    public void testUpdate_oneIdleDeveloper_oneFeatureToDevelop_noProgress() {
        createIdleDeveloper();
        Entity developmentEntity = makeFeatureInDevelopmentAndReturnDevelopmentEntity();
        advanceOneDay();
        thenThereIsNoProgressOn(developmentEntity);
    }

    private void createIdleDeveloper() {
        Entity developer = createEntityWithDevelopmentSkill(1).taskedWith(Task.IDLE);
        world.tasked.set(developer.id, Task.IDLE);
    }

    private DeveloperBuilder createEntityWithDevelopmentSkill(int devSkill) {
        return new DeveloperBuilder(devSkill);
    }

    private Entity createActiveDeveloper(int rate) {
        return createEntityWithDevelopmentSkill(rate).taskedWith(Task.DEVELOPMENT);
    }

    @Test
    public void testPositiveProgressRate_positiveProgress() {
        Entity entity = makeFeatureInDevelopmentAndReturnDevelopmentEntity();
        createActiveDeveloper(10);
        advanceOneDay();
        thenProgressIsPositiveOn(entity);
    }

    private void thenProgressIsPositiveOn(Entity entity) {
        assertTrue(world.developmentProgress.get(entity.id) > 0);
    }

    @Test
    public void testUpdate_defaultProgressUnitsArePerHour() {
        final float amountPerHour = 10;
        Entity entity = makeFeatureInDevelopmentAndReturnDevelopmentEntity();
        createActiveDeveloper(10);
        advanceOneHour();
        assertEquals(amountPerHour, world.developmentProgress.get(entity.id), EPSILON);
    }

    private void whenAFeatureIsCompleted() {
        completedDevelopmentEntity = makeFeatureInDevelopmentRequiring(0);
        createActiveDeveloper(10);
        advanceOneSecond();
    }

    private Entity makeFeatureInDevelopmentRequiring(int goal) {
        Entity e = makeFeatureInDevelopmentAndReturnDevelopmentEntity();
        e.add(world.goal);
        world.goal.set(e.id, goal);
        return e;
    }

    @Test
    public void testFeatureCompletion_removesDevelopmentEntity() {
        whenAFeatureIsCompleted();
        advanceOneMillisecond();
        assertTrue(completedDevelopmentEntity.isDisposed());
    }

    @Test
    public void testFeatureCompletion_enablesFeatureEntity() {
        whenAFeatureIsCompleted();
        thenTheDevelopedFeatureIsEnabled();
    }

    private void thenTheDevelopedFeatureIsEnabled() {
        final int featureId = world.featureId.get(completedDevelopmentEntity.id);
        final Entity featureEntity = world.entity(featureId);
        assertTrue(featureEntity.isEnabled());
    }

    private class DeveloperBuilder {
        private Entity entity = world.create(true)
                .add(world.developmentSkill,
                        world.tasked);

        public DeveloperBuilder(int devSkill) {
            world.developmentSkill.set(entity.id, devSkill);
        }

        public Entity taskedWith(int task) {
            world.tasked.set(entity.id, task);
            return entity;
        }
    }
}
