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

public final class Name {

    public static final class Builder {

        private final String first;

        private Builder(String first) {
            this.first = first;
        }

        public Name andLast(String last) {
            return new Name(first, first + " " + last);
        }
    }

    public static Name simply(String name) {
        return new Name(name, name);
    }

    public static Builder first(String first) {
        return new Builder(first);
    }

    public final String shortName;
    public final String fullName;

    private Name(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }


}
