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

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Shuffler {
    private static final Random random = new Random();

    public static <T> void shuffle(List<T> list) {
        // Algorithm from http://stackoverflow.com/questions/10052718/collection-shuffle-not-working-gwt
        for (int index = 0; index < list.size(); index += 1) {
            Collections.swap(list, index, index + random.nextInt(list.size() - index));
        }
    }
}
