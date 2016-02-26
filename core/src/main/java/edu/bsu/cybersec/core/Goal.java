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

public class Goal {
    public final int minimumUsers;
    public final float maximumExposure;

    public Goal(int minimumUsers, float maximumExposure) {
        this.minimumUsers = minimumUsers;
        this.maximumExposure = maximumExposure;
    }

    public boolean isMet(int numberOfUsers, float exposureLevel) {
        return isEnoughUsers(numberOfUsers) && isAcceptableExposure(exposureLevel);
    }

    public boolean isEnoughUsers(int numberOfUsers) {
        return numberOfUsers >= minimumUsers;
    }

    public boolean isAcceptableExposure(float exposure) {
        return exposure <= maximumExposure;
    }
}
