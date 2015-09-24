package edu.bsu.cybersec.core;

import org.junit.Test;
import org.mockito.Matchers;
import playn.core.Clock;
import tripleplay.entity.Entity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class UpdatingSystemTest extends AbstractSystemTest {

    private Updatable mockUpdatable;

    @Override
    public void setUp() {
        super.setUp();
        new UpdatingSystem(world);
        mockUpdatable = mock(Updatable.class);
    }

    @Test
    public void testUpdatableThingIsUpdated() {
        givenOneUpdatableEntity();
        advanceOneMillisecond();
        verify(mockUpdatable).update(Matchers.any(Clock.class));
    }

    private void givenOneUpdatableEntity() {
        Entity e = world.create(true)
                .add(world.onUpdate);
        world.onUpdate.set(e.id, mockUpdatable);
    }
}
