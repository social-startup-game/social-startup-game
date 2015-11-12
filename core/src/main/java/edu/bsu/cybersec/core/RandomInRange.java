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

import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

public class RandomInRange {
    private final int lowerBound;
    private final int upperBound;
    private final Random random = new Random();

    /**
     * @param lowerBound inclusive lower bound
     * @param upperBound inclusive upper bound
     */
    public RandomInRange(int lowerBound, int upperBound) {
        checkArgument(lowerBound <= upperBound);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }


    public int nextInt() {
        return random.nextInt(upperBound + 1 - lowerBound) + lowerBound;
    }
}
