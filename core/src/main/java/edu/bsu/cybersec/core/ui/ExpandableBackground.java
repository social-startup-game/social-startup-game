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

import edu.bsu.cybersec.core.systems.WorkHoursSystem;
import playn.core.Surface;
import playn.core.Tile;
import playn.scene.Layer;
import pythagoras.f.IDimension;
import tripleplay.ui.Background;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class ExpandableBackground extends Background {

    public static Builder background(Tile background) {
        return new Builder(background);
    }

    public static final class Builder {
        private Tile background;
        private WorkHoursSystem workHoursSystem;

        private Builder(Tile background) {
            this.background = checkNotNull(background);
        }

        public ExpandableBackground withWorkHours(WorkHoursSystem system) {
            this.workHoursSystem = checkNotNull(system);
            checkNotNull(background);
            return new ExpandableBackground(this);
        }
    }

    private final Tile background;
    private final WorkHoursSystem workHoursSystem;

    private ExpandableBackground(Builder builder) {
        this.background = builder.background.tile();
        this.workHoursSystem = builder.workHoursSystem;
    }

    @Override
    protected Instance instantiate(final IDimension size) {
        return new LayerInstance(size, new Layer() {
            private static final int MEDIUM_GREY = 0xff777777;
            private final float backgroundAspectRatio = background.width() / background.height();

            @Override
            protected void paintImpl(Surface surf) {
                paintBackground(surf);
                paintBorder(surf);
            }

            private void paintBackground(Surface surf) {
                checkState(backgroundAspectRatio <= 1, "Current approach assumes background image taller than wide");
                final Tile tile = background.tile();
                final float destinationX = 0;
                final float destinationY = 0;
                final float destinationWidth = size.width();
                final float destinationHeight = destinationWidth / backgroundAspectRatio;
                final float sourceX = 0;
                final float sourceY = 0;
                final float sourceWidth = tile.width();
                final float sourceHeight = tile.height();
                if (!workHoursSystem.isWorkHours().get()) {
                    surf.setTint(MEDIUM_GREY);
                }
                surf.draw(tile,
                        destinationX, destinationY, destinationWidth, destinationHeight,
                        sourceX, sourceY, sourceWidth, sourceHeight);
            }

            private void paintBorder(Surface surf) {
                surf.setFillColor(Colors.BLACK);
                final float y = size.height();
                surf.drawLine(0, y, size.width(), y, 4);
            }

        });
    }
}
