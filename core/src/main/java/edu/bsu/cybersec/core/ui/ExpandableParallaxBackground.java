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

import playn.core.Surface;
import playn.core.Tile;
import playn.core.TileSource;
import playn.scene.Layer;
import pythagoras.f.IDimension;
import tripleplay.ui.Background;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ExpandableParallaxBackground extends Background {

    public static Builder foreground(TileSource foreground) {
        return new Builder(foreground);
    }

    public static final class Builder {
        private final TileSource foreground;
        private TileSource background;

        private Builder(TileSource foreground) {
            this.foreground = checkNotNull(foreground);
        }

        public ExpandableParallaxBackground background(TileSource background) {
            this.background = checkNotNull(background);
            return new ExpandableParallaxBackground(this);
        }
    }

    private final TileSource foreground;
    private final TileSource background;

    private ExpandableParallaxBackground(Builder builder) {
        this.foreground = builder.foreground;
        this.background = builder.background;
    }

    @Override
    protected Instance instantiate(final IDimension size) {
        return new LayerInstance(size, new Layer() {
            private final float foregroundAspectRatio = foreground.tile().width() / foreground.tile().height();
            private final float characterWidthPercentOfScreen = 0.60f;

            @Override
            protected void paintImpl(Surface surf) {
                paintBackground(surf);
                paintForeground(surf);
                paintBorder(surf);
            }

            private void paintBackground(Surface surf) {
                final Tile tile = background.tile();
                final float destinationX = 0;
                final float destinationY = 0;
                final float destinationWidth = size.width();
                final float destinationHeight = size.height();
                final float sourceX = 0;
                final float sourceY = 0;
                final float sourceWidth = tile.width();
                final float sourceHeight = size.height();
                surf.draw(tile,
                        destinationX, destinationY, destinationWidth, destinationHeight,
                        sourceX, sourceY, sourceWidth, sourceHeight);
            }


            private void paintForeground(Surface surf) {
                final Tile tile = foreground.tile();
                final float destinationX = 0;
                final float destinationY = size.height() * 0.15f;
                final float destinationWidth = size.width() * characterWidthPercentOfScreen;
                final float destinationHeight = destinationWidth / foregroundAspectRatio;
                final float sourceX = 0;
                final float sourceY = 0;
                final float sourceWidth = tile.width();
                final float sourceHeight = tile.height();
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
