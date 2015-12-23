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
import edu.bsu.cybersec.core.ui.FontCache;
import playn.core.Font;
import tripleplay.ui.Group;

public interface Slide {

    Font FONT = FontCache.instance().REGULAR.derive(25);
    float GAP = SimGame.game.bounds.percentOfHeight(0.04f);

    boolean hasNext();

    Slide next();

    Group createUI();
}
