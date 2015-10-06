package edu.bsu.cybersec.core;

import org.junit.Test;
import react.Slot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class NarrativeEventTest extends AbstractSystemTest {

    @Test
    public void testRun_postsEventToGameWorld() {
        NarrativeEvent event = makeTestEvent();
        Slot<NarrativeEvent> slot = mockSlot();
        world.onNarrativeEvent.connect(slot);
        event.run();
        verify(slot).onEmit(event);
    }

    private NarrativeEvent makeTestEvent() {
        return new NarrativeEvent(world, "");
    }

    @SuppressWarnings("unchecked")
    private Slot<NarrativeEvent> mockSlot() {
        return mock(Slot.class);
    }
}
