package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public class UserGenerationSystemTest extends AbstractSystemTest {

    @Override
    public void setUp() {
        super.setUp();
        new UserGenerationSystem(world);
    }

    @Test
    public void testOnePerHour_GainOneUser() {
        createEntityGeneratingUsersPerHour(1);
        whenOneHourElapses();
        assertIntegerNumberOfUsersIs(1);
    }

    private void whenOneHourElapses() {
        advanceGameTimeToSimulateAFunctioningGameTimeSystem();
        advanceOneHour();
    }

    private void advanceGameTimeToSimulateAFunctioningGameTimeSystem() {
        world.prevGameTimeMs = world.gameTimeMs;
        world.gameTimeMs += ClockUtils.MS_PER_HOUR;
    }

    private Entity createEntityGeneratingUsersPerHour(float usersPerHour) {
        Entity entity = world.create(true)
                .add(world.usersPerHour, world.usersPerHourState);
        world.usersPerHourState.set(entity.id, UsersPerHourState.ACTIVE.value);
        world.usersPerHour.set(entity.id, usersPerHour);
        return entity;
    }

    private void assertIntegerNumberOfUsersIs(int users) {
        assertEquals(users, world.users.get().intValue(), EPSILON);
    }

    @Test
    public void testFivePerHour_gainFiveUsers() {
        createEntityGeneratingUsersPerHour(5);
        whenOneHourElapses();
        assertIntegerNumberOfUsersIs(5);
    }

    @Test
    public void testOnePerTwoHours_oneSecondsElapse_noUsers() {
        createEntityGeneratingUsersPerHour(0.5f);
        whenOneHourElapses();
        assertIntegerNumberOfUsersIs(0);
    }

    @Test
    public void testOnePerTwoHours_twoHoursElapse_oneUser() {
        createEntityGeneratingUsersPerHour(0.5f);
        whenOneHourElapses();
        whenOneHourElapses();
        assertIntegerNumberOfUsersIs(1);
    }

    @Test
    public void testUpdate_usersPerHourInactive_noChange() {
        Entity e = createEntityGeneratingUsersPerHour(5);
        world.usersPerHourState.set(e.id, UsersPerHourState.INACTIVE.value);
        whenOneHourElapses();
        assertIntegerNumberOfUsersIs(0);
    }
}
