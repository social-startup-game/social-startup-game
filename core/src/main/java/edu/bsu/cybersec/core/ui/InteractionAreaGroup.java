package edu.bsu.cybersec.core.ui;

import react.Value;
import react.ValueView;
import tripleplay.ui.Group;
import tripleplay.ui.Layout;

public class InteractionAreaGroup extends Group {

    protected Value<Boolean> needsAttention = Value.create(false);

    public InteractionAreaGroup(Layout layout) {
        super(layout);
    }

    public final ValueView<Boolean> onAttention() {
        return needsAttention;
    }

}
