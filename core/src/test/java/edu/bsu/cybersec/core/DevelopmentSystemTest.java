package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DevelopmentSystemTest extends AbstractSystemTest {

    @Override
    public void setUp() {
        super.setUp();
        new DevelopmentSystem(world);
    }

    @Test
    public void testUpdate_noDevelopers_noProgress() {
        FeatureInDevelopment inDevelopment = new FeatureInDevelopment();
        Entity featureEntity = world.create(true).add(world.featureInDevelopment);
        world.featureInDevelopment.set(featureEntity.id, inDevelopment);
        advanceOneDay();
        assertEquals(0, inDevelopment.progress);
    }

    @Test
    public void testUpdate_oneTaskedEntityIdle_oneFeatureToDevelop_noProgress() {
        createIdleDeveloper();
        FeatureInDevelopment inDevelopment = createFeatureInDevelopment();
        advanceOneDay();
        assertEquals(0, inDevelopment.progress);
    }

    @Test
    public void testUpdate_oneEntityDeveloping_oneFeatureToDevelop_developmentProgresses() {
        createActiveDeveloper();
        FeatureInDevelopment inDevelopment = createFeatureInDevelopment();
        advanceOneDay();
        assertTrue(inDevelopment.progress > 0);
    }

    private FeatureInDevelopment createFeatureInDevelopment() {
        FeatureInDevelopment inDevelopment = new FeatureInDevelopment();
        Entity featureEntity = world.create(true).add(world.featureInDevelopment);
        world.featureInDevelopment.set(featureEntity.id, inDevelopment);
        return inDevelopment;
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
