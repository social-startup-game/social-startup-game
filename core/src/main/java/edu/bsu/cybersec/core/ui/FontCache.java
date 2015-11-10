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

import playn.core.Font;
import playn.core.Graphics;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class FontCache {

    private static FontCache instance;
    private static ViewSize viewSize;

    public static FontCache initialize(Graphics g) {
        if (instance == null) {
            viewSize = new ViewSize(g);
            return instance = new FontCache();
        } else {
            return instance;
        }
    }

    public static FontCache instance() {
        checkState(instance != null, "Not yet initialized");
        return instance;
    }

    public final Font REGULAR = makeFont("Lato-Regular", 0.02f);

    private FontCache() {
    }

    private Font makeFont(String name, float percentOfHeight) {
        return new Font(name, viewSize.percent(percentOfHeight));
    }

    private static final class ViewSize {
        private final Graphics g;

        ViewSize(Graphics g) {
            this.g = checkNotNull(g);
        }

        public float percent(float percent) {
            return g.viewSize.height() * percent;
        }
    }
}
