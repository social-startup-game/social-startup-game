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

import org.junit.After;
import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class LearningSystemTest extends AbstractSystemTest {

    private Entity e;

    @Override
    public void setUp() {
        super.setUp();
        new LearningSystem(world);
    }

    @After
    public void tearDown() {
        e = null;
    }

    @Test
    public void testUpdate_development_developmentSkillIncreases() {
        givenAnActiveDeveloper();
        advancePlayNClockOneDay();
        assertTrue(world.developmentSkill.get(e.id) > 0);
    }

    private void givenAnActiveDeveloper() {
        e = makeWorker();
        world.tasked.set(e.id, Task.DEVELOPMENT);
    }

    private Entity makeWorker() {
        return world.create(true)
                .add(world.tasked, world.developmentSkill, world.maintenanceSkill);
    }

    @Test
    public void testUpdate_notDevelopment_developmentSkillUnchanged() {
        givenANonDevelopingWorker();
        advancePlayNClockOneDay();
        assertEquals(0, world.developmentSkill.get(e.id), EPSILON);
    }

    private void givenANonDevelopingWorker() {
        e = makeWorker();
    }

    @Test
    public void testUpdate_notMaintenance_maintenanceSkillUnchanged() {
        givenAnActiveDeveloper();
        advancePlayNClockOneDay();
        assertEquals(0, world.maintenanceSkill.get(e.id), EPSILON);
    }

    @Test
    public void testUpdate_maintenance_maintenanceSkillIncreased() {
        givenAnActiveMaintainer();
        advancePlayNClockOneDay();
        assertTrue(world.maintenanceSkill.get(e.id) > 0);
    }

    private void givenAnActiveMaintainer() {
        e = makeWorker();
        world.tasked.set(e.id, Task.MAINTENANCE);
    }
}
