package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public class UserGenerationSystemTest extends AbstractSystemTest {

    private UserGenerationSystem system;

    @Override
    public void setUp() {
        super.setUp();
        system = new UserGenerationSystem(world);
    }

    @Test
    public void testOnePerSecond_GainOneUser() {
        createEntityGeneratingUsersPerSecond(1);
        advanceOneSecond();
        assertNumberOfUsersIs(1);
    }

    private void createEntityGeneratingUsersPerSecond(float usersPerSecond) {
        Entity entity = world.create(true)
                .add(world.usersPerSecond);
        world.usersPerSecond.set(entity.id, usersPerSecond);
    }

    private void assertNumberOfUsersIs(int users) {
        assertEquals(users, system.users.get().intValue());
    }

    @Test
    public void testFivePerSecond_gainFiveUsers() {
        createEntityGeneratingUsersPerSecond(5);
        advanceOneSecond();
        assertNumberOfUsersIs(5);
    }

    @Test
    public void testOnePerTwoSeconds_oneSecondsElapse_noUsers() {
        createEntityGeneratingUsersPerSecond(0.5f);
        advanceOneSecond();
        assertNumberOfUsersIs(0);
    }

    @Test
    public void testOnePerTwoSeconds_twoSecondsElapse_oneUser() {
        createEntityGeneratingUsersPerSecond(0.5f);
        advanceOneSecond();
        advanceOneSecond();
        assertNumberOfUsersIs(1);
    }
}
