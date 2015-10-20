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

import com.google.common.collect.ImmutableList;

public final class CoreTask implements Task {
    public static final CoreTask MAINTENANCE = new CoreTask("Maintenance");
    public static final CoreTask DEVELOPMENT = new CoreTask("Development");
    public static final ImmutableList<CoreTask> VALUES = ImmutableList.of(DEVELOPMENT, MAINTENANCE);

    public static CoreTask forName(String name) {
        for (CoreTask t : VALUES) {
            if (t.name().equals(name)) return t;
        }
        throw new IllegalArgumentException("No such task: " + name);
    }

    private final String name;

    private CoreTask(String name) {
        this.name = name;
    }

    @Override
    public boolean isReassignable() {
        return true;
    }

    @Override
    public String name() {
        return name;
    }

}
