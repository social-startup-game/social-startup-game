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
import playn.core.Tile;
import react.Value;
import react.ValueView;
import tripleplay.ui.Background;
import tripleplay.ui.Group;
import tripleplay.ui.Layout;
import tripleplay.ui.Style;

public class InteractionAreaGroup extends Group {
    private static final Tile COMPANY_LOGO = SimGame.game.assets.getTile(GameAssets.ImageKey.COMPANY_LOGO_WITH_ALPHA);

    protected Value<Boolean> needsAttention = Value.create(false);

    public InteractionAreaGroup(Layout layout) {
        super(layout);
        applyCompanyLogoBackground();
    }

    public final ValueView<Boolean> onAttention() {
        return needsAttention;
    }

    private void applyCompanyLogoBackground() {
        Background b = Background.centered(COMPANY_LOGO);
        b = applyInsets(b);
        addStyles(Style.BACKGROUND.is(b));
    }

    protected Background applyInsets(Background b) {
        return b;
    }
}
