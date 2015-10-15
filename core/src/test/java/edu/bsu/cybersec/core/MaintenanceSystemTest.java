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

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class MaintenanceSystemTest extends AbstractSystemTest {

    private static final float ARBITRARY_SKILL = 1f;

    @Override
    public void setUp() {
        super.setUp();
        new MaintenanceSystem(world);
    }

    @Test
    public void testNoMaintainers_attackSurfaceUnchanged() {
        givenACompanyWithExposure(1.0f);
        whenOneDayOfGameTimeElapses();
        thenExposureIs(1.0f);
    }

    private void givenACompanyWithExposure(float exposure) {
        world.exposure.update(exposure);
    }

    private float currentExposure() {
        return world.exposure.get();
    }


    private void thenExposureIs(float surface) {
        assertEquals(surface, currentExposure(), EPSILON);
    }

    @Test
    public void testActiveMaintainer_reducesExposure() {
        final float initialSurface = 1.0f;
        givenACompanyWithExposure(initialSurface);
        givenAnActiveMaintainer(ARBITRARY_SKILL);
        whenOneDayOfGameTimeElapses();
        assertTrue(currentExposure() < initialSurface);
    }

    private Entity givenAnActiveMaintainer(float skill) {
        Entity maintainer = world.create(true)
                .add(world.tasked, world.maintenanceSkill);
        world.tasked.set(maintainer.id, Task.MAINTENANCE);
        world.maintenanceSkill.set(maintainer.id, skill);
        return maintainer;
    }

    @Test
    public void testIdleMaintainer_doesNotChangeExposure() {
        final float initialSurface = 1.0f;
        givenACompanyWithExposure(initialSurface);
        givenAnIdleMaintainer();
        whenOneDayOfGameTimeElapses();
        thenExposureIs(initialSurface);
    }

    private void givenAnIdleMaintainer() {
        Entity e = givenAnActiveMaintainer(ARBITRARY_SKILL);
        world.tasked.set(e.id, Task.IDLE);
        e.didChange();
    }

    @Test
    public void testUpdate_noGameTimeAdvance_noChangeInExposure() {
        final float initialSurface = 1.0f;
        givenACompanyWithExposure(initialSurface);
        givenAnActiveMaintainer(ARBITRARY_SKILL);
        advancePlayNClockOneDay();
        assertTrue(currentExposure() == initialSurface);
    }

    @Test
    public void testUpdate_maintenanceIsTenthsOfPercentPerHour() {
        final float initialSurface = 100.0f;
        final float skill = 5;
        givenACompanyWithExposure(initialSurface);
        givenAnActiveMaintainer(skill);
        whenOneHourOfGameTimeElapses();
        assertEquals(initialSurface - skill / 10f, currentExposure(), EPSILON);
    }

    @Test
    public void testUpdate_maintenanceIsTenthsOfPercentPerHour_integerSkillOnly() {
        final float initialSurface = 100.0f;
        final float skill = 5.9f;
        final float integerSkill = 5;
        givenACompanyWithExposure(initialSurface);
        givenAnActiveMaintainer(skill);
        whenOneHourOfGameTimeElapses();
        assertEquals(initialSurface - integerSkill / 10f, currentExposure(), EPSILON);
    }

}
