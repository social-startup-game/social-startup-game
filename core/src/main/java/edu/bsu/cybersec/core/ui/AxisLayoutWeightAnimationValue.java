package edu.bsu.cybersec.core.ui;

import tripleplay.anim.Animation;
import tripleplay.ui.Element;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.*;

public class AxisLayoutWeightAnimationValue implements Animation.Value {

    private final Element<?> element;

    public AxisLayoutWeightAnimationValue(Element<?> element) {
        this.element = checkNotNull(element);
    }

    @Override
    public float initial() {
        return 1;
    }

    @Override
    public void set(float value) {
        final AxisLayout.Constraint constraint = AxisLayout.stretched(value);
        element.setConstraint(constraint);
    }
}
