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
        givenAnActiveMaintainer();
        whenOneDayOfGameTimeElapses();
        assertTrue(currentExposure() < initialSurface);
    }

    private Entity givenAnActiveMaintainer() {
        Entity maintainer = world.create(true)
                .add(world.tasked, world.maintenanceSkill);
        world.tasked.set(maintainer.id, Task.MAINTENANCE);
        world.maintenanceSkill.set(maintainer.id, 1.0f);
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
        Entity e = givenAnActiveMaintainer();
        world.tasked.set(e.id, Task.IDLE);
        e.didChange();
    }

    @Test
    public void testUpdate_noGameTimeAdvance_noChangeInExposure() {
        final float initialSurface = 1.0f;
        givenACompanyWithExposure(initialSurface);
        givenAnActiveMaintainer();
        advancePlayNClockOneDay();
        assertTrue(currentExposure() == initialSurface);
    }

}
