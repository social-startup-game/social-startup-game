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

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import static com.google.common.base.Preconditions.checkArgument;

public final class TaskFormatter {

    private static final BiMap<Integer, String> MAP = ImmutableBiMap.of(
            Task.IDLE, "Idle",
            Task.DEVELOPMENT, "Development",
            Task.MAINTENANCE, "Maintenance");

    public String format(int task) {
        checkArgument(MAP.containsKey(task));
        return MAP.get(task);
    }

    public int asTask(String string) {
        checkArgument(MAP.containsValue(string));
        return MAP.inverse().get(string);
    }
}
