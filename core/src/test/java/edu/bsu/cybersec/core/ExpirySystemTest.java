package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class ExpirySystemTest extends AbstractSystemTest {

    private ExpirySystem system;

    @Override
    public void setUp() {
        super.setUp();
        system = new ExpirySystem(world);
    }

    @Test
    public void testExpiredItemIsRemoved() {
        Entity item = makeItemExpiringIn(0);
        advanceOneMillisecond();
        assertTrue(item.isDisposed());
    }

    private Entity makeItemExpiringIn(int ms) {
        Entity item = world.create(true)
                .add(world.expiresIn);
        world.expiresIn.set(item.id, ms);
        return item;
    }

    @Test
    public void testUnexpiredItemIsNotRemoved() {
        Entity item = makeItemExpiringIn(ClockUtils.MS_PER_DAY);
        advanceOneMillisecond();
        assertFalse(item.isDisposed());
    }

}
