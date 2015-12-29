/*
 * Copyright 2015 Paul Gestwicki
 *
 * This file is part of The Social Startup Game
 *
 * The Social Startup Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Social Startup Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Social Startup Game.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.bsu.cybersec.core.systems;

import edu.bsu.cybersec.core.FeatureFactory;
import edu.bsu.cybersec.core.GameTime;
import edu.bsu.cybersec.core.UsersPerHourState;
import org.junit.Assert;
import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.*;

public class FeatureDevelopmentSystemTest extends AbstractSystemTest {

    private static final float FEATURE_VULNERABILITY = 5f;
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

    private Entity makeFeatureInDevelopment() {
        Entity featureEntity = FeatureFactory.in(world).makeFeatureInDevelopment(0);
        world.usersPerHour.set(featureEntity.id, 20);
        world.developmentProgress.set(featureEntity.id, 0);
        world.goal.set(featureEntity.id, 100);
        world.vulnerability.set(featureEntity.id, FEATURE_VULNERABILITY);
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
        Entity idleTask = makeIdleTask();
        Entity developer = createEntityWithDevelopmentSkill(1).taskedWith(idleTask.id);
        world.task.set(developer.id, idleTask.id);
    }

    private DeveloperBuilder createEntityWithDevelopmentSkill(float devSkill) {
        return new DeveloperBuilder(devSkill);
    }

    private Entity createActiveDeveloper(float developmentSkill) {
        return createEntityWithDevelopmentSkill(developmentSkill).taskedWith(world.developmentTaskId);
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

    @Test
    public void testUpdate_defaultProgressUnitsArePerHour_roundedDownToWholeNumber() {
        Entity entity = makeFeatureInDevelopment();
        createActiveDeveloper(10.9f);
        whenOneHourOfGameTimeElapses();
        assertEquals(10, world.developmentProgress.get(entity.id), EPSILON);
    }

    @Test
    public void testUpdate_inefficiencyFactor_reducesProductivity() {
        final float inefficiencyFactor = 1 / 3f;
        system.inefficiencyFactor.update(inefficiencyFactor);
        Entity entity = makeFeatureInDevelopment();
        createActiveDeveloper(10f);
        whenOneHourOfGameTimeElapses();
        assertEquals(10 * inefficiencyFactor, world.developmentProgress.get(entity.id), EPSILON);
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
    public void testFeatureCompletion_addsVulnerabilityToExposure() {
        float initialExposure = currentExposure();
        whenAFeatureIsCompleted();
        whenOneDayOfGameTimeElapses();
        assertEquals(initialExposure + FEATURE_VULNERABILITY, currentExposure(), EPSILON);
    }

    private float currentExposure() {
        return world.exposure.get();
    }

    @Test
    public void testFeatureCompletion_enablesUserGeneration() {
        whenAFeatureIsCompleted();
        whenOneDayOfGameTimeElapses();
        Assert.assertEquals(UsersPerHourState.ACTIVE.value, world.usersPerHourState.get(completedDevelopmentEntity.id));
    }

    /**
     * Test that progress advancement is based on <em>game time</em>, not <em>clock time</em>.
     */
    @Test
    public void testUpdate_clockAdvanceWithoutGameTimeAdvance_noProgress() {
        Entity e = makeFeatureInDevelopment();
        createActiveDeveloper(1000);
        advancePlayNClockOneDay(); // NOT advancing game time
        world.gameTime.update(new GameTime(0, 0));
        thenThereIsNoProgressOn(e);
    }

    private final class DeveloperBuilder {
        private Entity entity = world.create(true)
                .add(world.developmentSkill,
                        world.task);

        public DeveloperBuilder(float devSkill) {
            world.developmentSkill.set(entity.id, devSkill);
        }

        public Entity taskedWith(int taskId) {
            world.task.set(entity.id, taskId);
            return entity;
        }
    }


}
