package edu.bsu.cybersec.core;

import org.junit.Before;
import org.junit.Test;
import playn.core.Clock;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;

public class UserAcquisitionSystemTest {

    private GameWorld world;
    private PlayNClockUtil clockUtil;
    private Entity company;

    @Before
    public void setUp() {
        world = new GameWorld();
        clockUtil = new PlayNClockUtil(world);
        createCompany();
    }

    private void createCompany() {
        company = world.create(true);
        company.add(world.company);
        world.company.set(company.id, new Company());
    }

    @Test
    public void testNullCase() {
        whenOneDayElapses();
        assertEquals(0, numberOfUsers());
    }

    private int numberOfUsers() {
        return world.company.get(company.id).users;
    }

    private void whenOneDayElapses() {
        clockUtil.advance(PlayNClockUtil.MS_PER_DAY);
    }

    private void whenMsElapses(int elapsedMS) {
        Clock playnClock = new Clock();
        playnClock.dt = elapsedMS;
        playnClock.tick += elapsedMS;
        world.update(playnClock);
    }
}
