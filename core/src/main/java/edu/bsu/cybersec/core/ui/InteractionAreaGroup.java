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
import playn.core.Tile;
import playn.scene.Layer;
import pythagoras.f.IDimension;
import react.Value;
import react.ValueView;
import tripleplay.ui.*;

public abstract class InteractionAreaGroup extends Group {
    private static final Tile COMPANY_LOGO = SimGame.game.assets.getTile(GameAssets.TileKey.COMPANY_LOGO_WITH_ALPHA);

    protected Value<Boolean> needsAttention = Value.create(false);

    public InteractionAreaGroup(Layout layout) {
        super(layout);
        applyCompanyLogoBackground();
    }

    public final ValueView<Boolean> onAttention() {
        return needsAttention;
    }

    private void applyCompanyLogoBackground() {
        Background b = new Background() {
            @Override
            protected Instance instantiate(final IDimension size) {
                return new LayerInstance(size, new Layer() {
                    @Override
                    protected void paintImpl(Surface surf) {
                        surf.draw(COMPANY_LOGO, 0, 0, size.width(), size.height());
                    }
                });
            }
        };
        b = applyInsets(b);
        addStyles(Style.BACKGROUND.is(b));
    }

    protected Background applyInsets(Background b) {
        return b;
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public static abstract class Scrolling extends InteractionAreaGroup {

        protected Scroller scroller;

        public Scrolling(Layout layout) {
            super(layout);
        }

        @Override
        protected void validate() {
            super.validate();
            updateScrollerConstraints();
        }

        private void updateScrollerConstraints() {
            final IDimension parentSize = _parent.size();
            scroller.setConstraint(Constraints.fixedSize(parentSize.width(), parentSize.height()));
        }

        @Override
        protected void wasParented(Container<?> parent) {
            super.wasParented(parent);
            updateScrollerConstraints();
        }

        @Override
        protected Background applyInsets(Background b) {
            float horizontal = SimGame.game.plat.graphics().viewSize.height() * 0.02f;
            return b.inset(horizontal, 0f);
        }
    }
}
