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

package edu.bsu.cybersec.core.intro;

import edu.bsu.cybersec.core.SimGame;
import playn.core.Tile;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class TextAndImageSlide implements Slide {

    private final String text;
    private final Tile background;

    public TextAndImageSlide(String text, Tile background) {
        this.text = checkNotNull(text);
        this.background = checkNotNull(background);
    }

    public Group createUI() {
        return new SizableGroup(AxisLayout.vertical(), SimGame.game.bounds.width(), SimGame.game.bounds.height())
                .add(new Label(text, Icons.image(background))
                        .addStyles(Style.TEXT_WRAP.on,
                                Style.FONT.is(FONT),
                                Style.ICON_POS.below,
                                Style.ICON_GAP.is((int) GAP)));
    }
}
