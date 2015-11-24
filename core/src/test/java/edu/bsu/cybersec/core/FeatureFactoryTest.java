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

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.*;

public class FeatureFactoryTest {

    private static final int ARBITRARY_FEATURE_NUMBER = 5;

    private GameWorld world;

    @Before
    public void setUp() {
        world = new GameWorld();
    }

    @Test
    public void testMakeFeatureInDevelopment_associatesFeatureNumberWithEntity() {
        final GameWorld world = new GameWorld();
        Entity e = FeatureFactory.in(world).makeFeatureInDevelopment(ARBITRARY_FEATURE_NUMBER);
        assertEquals(ARBITRARY_FEATURE_NUMBER, world.featureNumber.get(e.id));
    }

    @Test
    public void testMakeFeatureInDevelopment_hasVulnerability() {
        Entity e = FeatureFactory.in(world).makeFeatureInDevelopment(ARBITRARY_FEATURE_NUMBER);
        assertTrue(world.vulnerability.get(e.id) > 0);
    }

    @Test
    public void testMakeFeatureInDevelopment_allFeatureNamesUsedBeforeRepeat() {
        int timesToRunTest = 20;
        for (int i = 0; i < timesToRunTest; i++) {
            GameWorld world = new GameWorld();
            FeatureFactory featureFactory = FeatureFactory.in(world).withNames(ImmutableList.of("One", "Two"));
            Entity e1 = featureFactory.makeFeatureInDevelopment(ARBITRARY_FEATURE_NUMBER);
            Entity e2 = featureFactory.makeFeatureInDevelopment(ARBITRARY_FEATURE_NUMBER + 1);
            assertNotEquals(world.name.get(e1.id), world.name.get(e2.id));
        }
    }
}
