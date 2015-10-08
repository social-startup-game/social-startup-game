package edu.bsu.cybersec.core;

import org.junit.Test;
import playn.core.Clock;
import tripleplay.entity.Entity;
import tripleplay.entity.System;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureGenerationSystemTest extends AbstractSystemTest {

    private InDevelopmentFeatureCountingSystem countingSystem;

    @Override
    public void setUp() {
        super.setUp();
        new FeatureGenerationSystem(world);
        countingSystem = new InDevelopmentFeatureCountingSystem(world);
    }

    /**
     * Check that the class we use to verify our tests works as expected.
     */
    @Test
    public void testInDevelopmentFeatureCountingSystem() {
        world.create(true).add(world.developmentProgress);
        advanceOneMillisecond();
        assertEquals(1, countingSystem.lastCount);
    }

    @Test
    public void testUpdate_noFeaturesInDevelopment_oneIsGenerated() {
        advanceTimeTwiceSinceWeCannotGuaranteeTheOrderOfUpdates();
        assertTrue("Expected positive count but was " + countingSystem.lastCount, countingSystem.lastCount > 0);
    }

    private void advanceTimeTwiceSinceWeCannotGuaranteeTheOrderOfUpdates() {
        advanceOneMillisecond();
        advanceOneMillisecond();
    }

    private class InDevelopmentFeatureCountingSystem extends System {

        public int lastCount;
        private final GameWorld world;

        public InDevelopmentFeatureCountingSystem(GameWorld gameWorld) {
            super(gameWorld, SystemPriority.MODEL_LEVEL.value);
            this.world = gameWorld;
        }

        @Override
        protected boolean isInterested(Entity entity) {
            return entity.has(world.developmentProgress);
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            super.update(clock, entities);
            lastCount = entities.size();
        }
    }
}
