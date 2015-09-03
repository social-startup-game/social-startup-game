package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class MaintenanceSystemTest extends AbstractSystemTest {

    private Entity company;
    private MaintenanceSystem system;

    @Override
    public void setUp() {
        super.setUp();
        company = world.create(true)
                .add(world.attackSurface);
        world.attackSurface.set(company.id, 0f);
        system = new MaintenanceSystem(world);
    }

    @Test
    public void testNoMaintainers_attackSurfaceUnchanged() {
        givenACompanyWithAttackSurface(1.0f);
        advanceOneDay();
        thenAttackSurfaceIs(1.0f);
    }

    private void givenACompanyWithAttackSurface(float surface) {
        world.attackSurface.set(company.id, surface);
    }

    private float currentAttackSurface() {
        return world.attackSurface.get(company.id);
    }


    private void thenAttackSurfaceIs(float surface) {
        assertEquals(surface, currentAttackSurface(), EPSILON);
    }

    @Test
    public void testActiveMaintainer_reducesAttackSurface() {
        final float initialSurface = 1.0f;
        givenACompanyWithAttackSurface(initialSurface);
        givenAnActiveMaintainer();
        advanceOneDay();
        assertTrue("Attack surface is reduced", currentAttackSurface() < initialSurface);
    }

    private Entity givenAnActiveMaintainer() {
        Entity maintainer = world.create(true)
                .add(world.tasked, world.maintenanceSkill, world.companyId);
        world.tasked.set(maintainer.id, Task.MAINTENANCE);
        world.maintenanceSkill.set(maintainer.id, 1.0f);
        world.companyId.set(maintainer.id, company.id);
        return maintainer;
    }

    @Test
    public void testIdleMaintainer_doesNotChangeAttackSurface() {
        final float initialSurface = 1.0f;
        givenACompanyWithAttackSurface(initialSurface);
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
