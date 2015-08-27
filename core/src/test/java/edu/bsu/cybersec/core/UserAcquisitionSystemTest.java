package edu.bsu.cybersec.core;

import org.junit.Test;
import playn.core.Clock;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public class UserAcquisitionSystemTest extends AbstractSystemTest {

    private UserAcquisitionSystem system;
    private Entity company;

    @Override
    public void setUp() {
        super.setUp();
        system = new UserAcquisitionSystem(world);
        createCompany();
    }

    private void createCompany() {
        company = world.create(true);
        company.add(world.company);
        world.company.set(company.id, new Company());
    }

    @Test
    public void testZeroFeatures() {
        advanceOneDay();
        assertEquals(0, numberOfUsers());
    }

    private int numberOfUsers() {
        return world.company.get(company.id).users;
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
        advanceOneDay();
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
        advanceOneDay();
        assertEquals(5, numberOfUsers());
    }
}
