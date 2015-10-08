package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.*;

public class FeatureDevelopmentSystemTest extends AbstractSystemTest {

    private Entity completedDevelopmentEntity;
    private FeatureDevelopmentSystem system;

    @Override
    public void setUp() {
        super.setUp();
        system = new FeatureDevelopmentSystem(world);
        completedDevelopmentEntity = null;
    }

    @Test
    public void testUpdate_noDevelopers_noProgress() {
        Entity developmentEntity = makeFeatureInDevelopment();
        whenOneDayOfGameTimeElapses();
        thenThereIsNoProgressOn(developmentEntity);
    }

    private void whenOneDayOfGameTimeElapses() {
        world.advanceGameTime(ClockUtils.MS_PER_DAY);
        advanceOneDay();
    }

    private void whenOneHourOfGameTimeElapses() {
        world.advanceGameTime(ClockUtils.MS_PER_HOUR);
        advanceOneHour();
    }

    private Entity makeFeatureInDevelopment() {
        Entity featureEntity = system.makeFeatureEntity();
        world.usersPerHour.set(featureEntity.id, 20);
        world.developmentProgress.set(featureEntity.id, 0);
        world.goal.set(featureEntity.id, 100);
        world.vulnerability.set(featureEntity.id, 10);
        world.vulnerabilityState.set(featureEntity.id, VulnerabilityState.INACTIVE.value);
        return featureEntity;
    }

    private void thenThereIsNoProgressOn(Entity developmentEntity) {
        assertEquals(0, world.developmentProgress.get(developmentEntity.id), EPSILON);
    }

    @Test
    public void testUpdate_oneIdleDeveloper_oneFeatureToDevelop_noProgress() {
        createIdleDeveloper();
        Entity developmentEntity = makeFeatureInDevelopment();
        whenOneDayOfGameTimeElapses();
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
        Entity entity = makeFeatureInDevelopment();
        createActiveDeveloper(10);
        whenOneDayOfGameTimeElapses();
        thenProgressIsPositiveOn(entity);
    }

    private void thenProgressIsPositiveOn(Entity entity) {
        assertTrue(world.developmentProgress.get(entity.id) > 0);
    }

    @Test
    public void testUpdate_defaultProgressUnitsArePerHour() {
        Entity entity = makeFeatureInDevelopment();
        createActiveDeveloper(10);
        whenOneHourOfGameTimeElapses();
        assertEquals(10, world.developmentProgress.get(entity.id), EPSILON);
    }

    private void whenAFeatureIsCompleted() {
        completedDevelopmentEntity = makeFeatureInDevelopmentRequiring(0);
        createActiveDeveloper(10);
        whenOneDayOfGameTimeElapses();
    }

    private Entity makeFeatureInDevelopmentRequiring(int goal) {
        Entity e = makeFeatureInDevelopment();
        e.add(world.goal);
        world.goal.set(e.id, goal);
        return e;
    }

    @Test
    public void testFeatureCompletion_removesDevelopmentProgressComponent() {
        whenAFeatureIsCompleted();
        whenOneDayOfGameTimeElapses();
        assertFalse(completedDevelopmentEntity.has(world.developmentProgress));
    }

    @Test
    public void testFeatureCompletion_enablesVulnerability() {
        whenAFeatureIsCompleted();
        whenOneDayOfGameTimeElapses();
        assertEquals(VulnerabilityState.ACTIVE.value, world.vulnerabilityState.get(completedDevelopmentEntity.id));
    }

    /**
     * Test that progress advancement is based on <em>game time</em>, not <em>clock time</em>.
     */
    @Test
    public void testUpdate_clockAdvanceWithoutGameTimeAdvance_noProgress() {
        Entity e = makeFeatureInDevelopment();
        createActiveDeveloper(1000);
        advanceOneDay(); // NOT advancing game time
        world.gameTimeMs = world.prevGameTimeMs = 0;
        thenThereIsNoProgressOn(e);
    }

    private final class DeveloperBuilder {
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
