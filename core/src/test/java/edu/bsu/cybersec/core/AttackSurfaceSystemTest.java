package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class AttackSurfaceSystemTest extends AbstractSystemTest {

    private AttackSurfaceSystem system;
    private Entity company;

    @Override
    public void setUp() {
        super.setUp();
        system = new AttackSurfaceSystem(world);
        company = world.create(true);
    }

    @Test
    public void testUpdate_noEntitiesWithExposure_noAttackSurface() {
        givenACompanyEntityWithZeroAttackSurface();
        advanceOneDay();
        thenTheCompanyAttackSurfaceIs(0);
    }

    private void givenACompanyEntityWithZeroAttackSurface() {
        company = world.create(true)
                .add(world.attackSurface);
        world.attackSurface.set(company.id, 0);
    }

    private void thenTheCompanyAttackSurfaceIs(float surface) {
        assertEquals(surface, world.attackSurface.get(company.id), EPSILON);
    }

    @Test
    public void testUpdate_oneEntityWithZeroExposure_noAttackSurfaceChange() {
        givenACompanyEntityWithZeroAttackSurface();
        givenAnEntityOwnedByTheCompanyWithExposure(0);
        advanceOneDay();
        thenTheCompanyAttackSurfaceIs(0);
    }

    private void givenAnEntityOwnedByTheCompanyWithExposure(float exposure) {
        Entity exposureEntity = world.create(true)
                .add(world.exposure, world.owner);
        world.exposure.set(exposureEntity.id, exposure);
        world.owner.set(exposureEntity.id, company.id);
    }

    @Test
    public void testUpdate_oneEntityWithPositiveExposure_attackSurfaceIsPositive() {
        givenACompanyEntityWithZeroAttackSurface();
        givenAnEntityOwnedByTheCompanyWithExposure(10);
        advanceOneDay();
        thenTheCompanyAttackSurfaceIsPositive();
    }

    private void thenTheCompanyAttackSurfaceIsPositive() {
        assertTrue(world.attackSurface.get(company.id) > 0);
    }

}