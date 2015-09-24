package edu.bsu.cybersec.core;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.mockito.Mockito.*;

public final class EventTriggerSystemTest extends AbstractSystemTest {

    private Runnable runnable;

    @Override
    public void setUp() {
        super.setUp();
        new EventTriggerSystem(world);
        runnable = mock(Runnable.class);
    }

    @Test
    public void testEvenTriggersWhenTimePassesIt() {
        givenAnEventThatTriggersInMilliseconds(1);
        whenOneMsAdvances();
        thenTheEventTriggers();
    }

    private void whenOneMsAdvances() {
        // We manually fiddle with the gameTimeMs here since the unit test runs independently of any GameTimeSystem.
        world.advanceGameTime(1);
        advanceOneMillisecond();
    }

    private void givenAnEventThatTriggersInMilliseconds(int ms) {
        Entity e = world.create(true)
                .add(world.timeTrigger, world.event);
        world.timeTrigger.set(e.id, world.gameTimeMs + ms);
        world.event.set(e.id, runnable = mock(Runnable.class));
    }

    private void thenTheEventTriggers() {
        verify(runnable).run();
    }

    @Test
    public void testEventDoesNotTriggerBeforeItsTime() {
        givenAnEventThatTriggersInMilliseconds(10);
        whenOneMsAdvances();
        thenTheEventDoesNotTrigger();
    }

    private void thenTheEventDoesNotTrigger() {
        verifyZeroInteractions(runnable);
    }

    @Test
    public void testEventDoesNotTriggerMoreThanOnce() {
        givenAnEventThatTriggersInMilliseconds(1);
        whenOneMsAdvances();
        whenOneMsAdvances();
        verify(runnable, times(1)).run();
    }
}
