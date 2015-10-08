package edu.bsu.cybersec.core;

import org.junit.Before;
import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}
