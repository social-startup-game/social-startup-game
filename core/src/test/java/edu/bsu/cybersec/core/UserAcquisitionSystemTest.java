package edu.bsu.cybersec.core;

import org.junit.Before;
import org.junit.Test;
import playn.core.Clock;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public class UserAcquisitionSystemTest {

    private GameWorld world;
    private UserAcquisitionSystem system;
    private PlayNClockUtil clockUtil;
    private Entity company;

    @Before
    public void setUp() {
        world = new GameWorld();
        system = new UserAcquisitionSystem(world);
        clockUtil = new PlayNClockUtil(world);
        createCompany();
    }

    private void createCompany() {
        company = world.create(true);
        company.add(world.company);
        world.company.set(company.id, new Company());
    }

    @Test
    public void testZeroFeatures() {
        whenOneDayElapses();
        assertEquals(0, numberOfUsers());
    }

    private int numberOfUsers() {
        return world.company.get(company.id).users;
    }

    private void whenOneDayElapses() {
        clockUtil.advance(ClockUtils.MS_PER_DAY);
    }

    private void whenMsElapses(int elapsedMS) {
        Clock playnClock = new Clock();
        playnClock.dt = elapsedMS;
        playnClock.tick += elapsedMS;
        world.update(playnClock);
    }

    @Test
    public void testOneFeatureAndOneDayElapses_GainOneUser() {
        createFeatureGeneratingUsersPerDay(1);
        whenOneDayElapses();
        assertEquals(1, numberOfUsers());
    }

    private void createFeatureGeneratingUsersPerDay(float usersPerDay) {
        Feature feature = new Feature();
        feature.usersPerDay = usersPerDay;
        feature.companyId = company.id;
        Entity featureEntity = world.create(true)
                .add(world.feature);
        world.feature.set(featureEntity.id, feature);
    }

    @Test
    public void testOneFeatureOneDayElapses_GainFiveUsers() {
        createFeatureGeneratingUsersPerDay(5);
        whenOneDayElapses();
        assertEquals(5, numberOfUsers());
    }
}
