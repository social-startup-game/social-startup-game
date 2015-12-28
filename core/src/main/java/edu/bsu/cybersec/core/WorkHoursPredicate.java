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

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class WorkHoursPredicate implements Predicate<Integer> {

    private static final WorkHoursPredicate SINGLETON = new WorkHoursPredicate();

    public static WorkHoursPredicate instance() {
        return SINGLETON;
    }

    private static final int WORK_DAY_DURATION = 9;

    private WorkHoursPredicate() {
    }

    @Override
    public boolean apply(@Nullable Integer seconds) {
        // @Nullable comes from the Predicate interface, but we really don't want to deal with nulls here.
        checkNotNull(seconds);
        int elapsedHours = seconds / ClockUtils.SECONDS_PER_HOUR;
        return elapsedHours % 24 < WORK_DAY_DURATION;
    }

}
