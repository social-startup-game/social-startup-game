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

import edu.bsu.cybersec.core.SimGame;
import playn.core.Surface;
import playn.scene.Layer;

import static com.google.common.base.Preconditions.checkArgument;

public final class ProgressBar extends Layer {
    private int value;
    private final int max;
    private final float width;
    private final float height;

    public ProgressBar(int max, float width, float height) {
        checkArgument(max >= 0);
        this.max = max;
        this.width = width;
        this.height = height;
    }

    public void increment() {
        value++;
        if (value > max) {
            SimGame.game.plat.log().warn("Value (" + value + ") exceeds max (" + max + "); capping.");
            value = max;
        }
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }

    @Override
    protected void paintImpl(Surface surf) {
        final float percent = value / (float) max;
        surf.setFillColor(Palette.UNUSED_SPACE);
        surf.fillRect(0, 0, width(), height());
        surf.setFillColor(Palette.FOREGROUND);
        surf.fillRect(0, 0, width() * percent, height());
    }
}
