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
import tripleplay.ui.Label;
import tripleplay.ui.Style;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

public class LabelColorHighlightAmount implements Animation.Value {

    private static final int HIGHLIGHT_COLOR = Palette.FOREGROUND;
    private static final int REGULAR_COLOR = Palette.NAME_COLOR;

    private final Label label;

    public LabelColorHighlightAmount(Label label) {
        this.label = checkNotNull(label);
    }

    @Override
    public float initial() {
        return 0;
    }

    @Override
    public void set(float value) {
        int current = Colors.blend(HIGHLIGHT_COLOR, REGULAR_COLOR, value);
        label.addStyles(Style.COLOR.is(current));
    }
}
