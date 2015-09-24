package edu.bsu.cybersec.core.ui;

import playn.core.Surface;
import playn.core.Tile;
import playn.core.TileSource;
import playn.scene.Layer;
import pythagoras.f.IDimension;
import tripleplay.ui.Background;

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
            @Override
            protected void paintImpl(Surface surf) {
                paintBackground(surf);
                paintForeground(surf);
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
                final float destinationWidth = tile.width();
                final float destinationHeight = size.height();
                final float sourceX = 0;
                final float sourceY = 0;
                final float sourceWidth = tile.width();
                final float sourceHeight = size.height();
                surf.draw(tile,
                        destinationX, destinationY, destinationWidth, destinationHeight,
                        sourceX, sourceY, sourceWidth, sourceHeight);
            }

        });
    }
}
