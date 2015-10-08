package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public class UserGenerationSystemTest extends AbstractSystemTest {

    private int companyId;

    @Override
    public void setUp() {
        super.setUp();
        new UserGenerationSystem(world);
        companyId = createCompanyEntity();
    }

    private int createCompanyEntity() {
        Entity e = world.create(true).add(world.users);
        return e.id;
    }

    @Test
    public void testOnePerHour_GainOneUser() {
        createEntityGeneratingUsersPerHour(1);
        whenOneHourElapses();
        assertNumberOfUsersIs(1);
    }

    private void whenOneHourElapses() {
        advanceGameTimeToSimulateAFunctioningGameTimeSystem();
        advanceOneHour();
    }

    private void advanceGameTimeToSimulateAFunctioningGameTimeSystem() {
        world.prevGameTimeMs = world.gameTimeMs;
        world.gameTimeMs += ClockUtils.MS_PER_HOUR;
    }

    private void createEntityGeneratingUsersPerHour(float usersPerHour) {
        Entity entity = world.create(true)
                .add(world.usersPerHour, world.companyId);
        world.usersPerHour.set(entity.id, usersPerHour);
        world.companyId.set(entity.id, companyId);
    }

    private void assertNumberOfUsersIs(int users) {
        int actualAsInt = (int) world.users.get(companyId);
        assertEquals(users, actualAsInt);
    }

    @Test
    public void testFivePerHour_gainFiveUsers() {
        createEntityGeneratingUsersPerHour(5);
        whenOneHourElapses();
        assertNumberOfUsersIs(5);
    }

    @Test
    public void testOnePerTwoHours_oneSecondsElapse_noUsers() {
        createEntityGeneratingUsersPerHour(0.5f);
        whenOneHourElapses();
        assertNumberOfUsersIs(0);
    }

    @Test
    public void testOnePerTwoHours_twoHoursElapse_oneUser() {
        createEntityGeneratingUsersPerHour(0.5f);
        whenOneHourElapses();
        whenOneHourElapses();
        assertNumberOfUsersIs(1);
    }
}
