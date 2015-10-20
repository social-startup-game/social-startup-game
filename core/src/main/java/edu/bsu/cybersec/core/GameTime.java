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

package edu.bsu.cybersec.core;

import static com.google.common.base.Preconditions.checkArgument;

public final class GameTime {
    public final int now;
    public final int previous;

    public GameTime(int now, int previous) {
        checkArgument(previous <= now, "Previous may not be after now.");
        this.now = now;
        this.previous = previous;
    }

    public GameTime(GameTime previous, int elapsed) {
        this.previous = previous.now;
        this.now = previous.now + elapsed;
    }

    public int delta() {
        return now - previous;
    }

    public boolean contains(int i) {
        return previous <= i && i <= now;
    }
}
