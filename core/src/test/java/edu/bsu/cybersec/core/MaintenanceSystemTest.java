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
        advanceOneDay();
        thenAttackSurfaceIs(1.0f);
    }

    private void givenACompanyWithExposure(float exposure) {
        world.exposure.update(exposure);
    }

    private float currentExposure() {
        return world.exposure.get();
    }


    private void thenAttackSurfaceIs(float surface) {
        assertEquals(surface, currentExposure(), EPSILON);
    }

    @Test
    public void testActiveMaintainer_reducesExposure() {
        final float initialSurface = 1.0f;
        givenACompanyWithExposure(initialSurface);
        givenAnActiveMaintainer();
        advanceOneDay();
        assertTrue("Attack surface is reduced", currentExposure() < initialSurface);
    }

    private Entity givenAnActiveMaintainer() {
        Entity maintainer = world.create(true)
                .add(world.tasked, world.maintenanceSkill);
        world.tasked.set(maintainer.id, Task.MAINTENANCE);
        world.maintenanceSkill.set(maintainer.id, 1.0f);
        return maintainer;
    }

    @Test
    public void testIdleMaintainer_doesNotChangeAttackSurface() {
        final float initialSurface = 1.0f;
        givenACompanyWithExposure(initialSurface);
        givenAnIdleMaintainer();
        advanceOneDay();
        thenAttackSurfaceIs(initialSurface);
    }

    private void givenAnIdleMaintainer() {
        Entity e = givenAnActiveMaintainer();
        world.tasked.set(e.id, Task.IDLE);
        e.didChange();
    }

}
