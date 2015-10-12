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

package edu.bsu.cybersec.core.ui;

import tripleplay.anim.Animation;
import tripleplay.ui.Element;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.checkNotNull;

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
