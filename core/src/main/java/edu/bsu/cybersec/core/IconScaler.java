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

package edu.bsu.cybersec.core;

import edu.bsu.cybersec.core.ui.GameAssets;
import playn.core.Image;
import tripleplay.ui.Icon;
import tripleplay.ui.Icons;

import static com.google.common.base.Preconditions.checkNotNull;

public final class IconScaler {

    private final SimGame game;

    public IconScaler(SimGame game) {
        this.game = checkNotNull(game);
    }

    public Icon scale(GameAssets.ImageKey key, float desiredWidth) {
        final Image image = game.assets.getImage(key);
        final float scale = desiredWidth / image.width();
        return Icons.scaled(Icons.image(image), scale);
    }
}
