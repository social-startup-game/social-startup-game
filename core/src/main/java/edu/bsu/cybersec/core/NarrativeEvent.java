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


import static com.google.common.base.Preconditions.checkNotNull;

public class NarrativeEvent implements Runnable {

    public static class Option {
        public final String text;
        public final Runnable action;

        public Option(String text, Runnable action) {
            this.text = text;
            this.action = action;
        }
    }

    private final GameWorld gameWorld;
    public final String text;
    public final Option[] options;

    public NarrativeEvent(GameWorld world, String text, Option... options) {
        this.gameWorld = checkNotNull(world);
        this.text = text;
        this.options = options;
    }

    @Override
    public void run() {
        gameWorld.onNarrativeEvent.emit(this);
    }
}
