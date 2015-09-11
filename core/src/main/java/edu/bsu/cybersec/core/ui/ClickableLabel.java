package edu.bsu.cybersec.core.ui;

import playn.scene.Pointer;
import react.Signal;
import react.Slot;
import tripleplay.ui.Behavior;
import tripleplay.ui.Label;

public class ClickableLabel extends Label {

    private final Signal<ClickableLabel> onClick = Signal.create();

    public ClickableLabel(String text) {
        super(text);
    }

    public ClickableLabel onClick(Slot<? super ClickableLabel> slot) {
        onClick.connect(slot);
        return this;
    }

    @Override
    protected Behavior<Label> createBehavior() {
        return new Behavior<Label>(this) {

            @Override
            public void onPress(Pointer.Interaction iact) {
                // ignore
            }

            @Override
            public void onHover(Pointer.Interaction iact, boolean inBounds) {
                // ignore
            }

            @Override
            public boolean onRelease(Pointer.Interaction iact) {
                return true;
            }

            @Override
            public void onClick(Pointer.Interaction iact) {
                onClick.emit(ClickableLabel.this);
            }
        };
    }
}
