/*
 * Copyright 2016 Paul Gestwicki
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

import playn.scene.Pointer;
import react.Signal;
import react.Slot;
import tripleplay.ui.Behavior;
import tripleplay.ui.Label;

public class TouchableLabel extends Label {

    private final Signal<TouchableLabel> onTouch = Signal.create();

    public TouchableLabel(String name) {
        super(name);
    }

    public TouchableLabel onClick(Slot<? super TouchableLabel> slot) {
        onTouch.connect(slot);
        return this;
    }

    @Override
    protected Behavior<Label> createBehavior() {
        return new Behavior<Label>(this) {

            @Override
            public void onPress(Pointer.Interaction iact) {
                if (iact.event.kind == playn.core.Pointer.Event.Kind.START
                        && !iact.event.isTouch) {
                    onTouch.emit(TouchableLabel.this);
                }
            }

            @Override
            public void onHover(Pointer.Interaction iact, boolean inBounds) {
                // ignore
            }

            @Override
            public boolean onRelease(Pointer.Interaction iact) {
                // ignore---there are no clicks
                return false;
            }

            @Override
            public void onClick(Pointer.Interaction iact) {
                // ignore
            }
        };
    }
}
