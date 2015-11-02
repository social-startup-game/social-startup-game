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
        whenSomeTimeElapses();
        assertEquals(1, countingSystem.lastCount);
    }

    @Test
    public void testUpdate_noFeaturesInDevelopment_oneIsGenerated() {
        advanceTimeTwiceSinceWeCannotGuaranteeTheOrderOfUpdates();
        assertTrue("Expected positive count but was " + countingSystem.lastCount, countingSystem.lastCount > 0);
    }

    private void advanceTimeTwiceSinceWeCannotGuaranteeTheOrderOfUpdates() {
        whenSomeTimeElapses();
        whenSomeTimeElapses();
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
