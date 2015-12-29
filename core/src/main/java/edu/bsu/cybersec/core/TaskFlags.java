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

public enum TaskFlags {

    MAINTENANCE(1),
    DEVELOPMENT(1 << 1),
    REASSIGNABLE(1 << 2),
    BOUND_TO_WORKDAY(1 << 3),
    NOT_AT_WORK(1 << 4);

    private final int bit;

    TaskFlags(int bit) {
        this.bit = bit;
    }

    public boolean isSet(int value) {
        return (value & bit) != 0;
    }

    public int set(int onto) {
        return onto | bit;
    }

    public static int flags(TaskFlags... flags) {
        int result = 0;
        for (TaskFlags flag : flags) {
            result |= flag.bit;
        }
        return result;
    }

    public static FlagQuery any(TaskFlags... flags) {
        return new FlagQuery(flags);
    }

    public static final class FlagQuery {

        private final TaskFlags[] flags;

        private FlagQuery(TaskFlags[] flags) {
            this.flags = flags;
        }

        public boolean in(int value) {
            for (TaskFlags flag : flags) {
                if (flag.isSet(value)) return true;
            }
            return false;
        }
    }
}
