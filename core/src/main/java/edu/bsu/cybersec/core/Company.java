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

import com.google.common.collect.ImmutableList;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Company {

    public final ImmutableList<Employee> employees;
    public final Employee boss;
    public final Goal goal = new Goal(20000, 0.15f);

    public static Builder from(EmployeePool employeePool) {
        return new Builder(employeePool);
    }

    public static final class Builder {
        private final EmployeePool pool;

        private Builder(EmployeePool pool) {
            this.pool = checkNotNull(pool);
        }

        public Company withEmployees(int i) {
            checkArgument(i >= 0, "Must recruit non-negative numbers");
            final Employee boss = pool.removeOne();
            return new Company(boss, pool.recruit(i));
        }
    }

    private Company(Employee boss, Set<Employee> recruits) {
        checkArgument(!recruits.contains(boss), "The boss cannot also be a worker");
        this.boss = checkNotNull(boss);
        employees = ImmutableList.copyOf(recruits);
        if (SimGame.game != null) {
            SimGame.game.event.emit(TrackedEvent.game().action("CEO").label(boss.profile.firstName));
            for (Employee e : employees) {
                SimGame.game.event.emit(TrackedEvent.game().action("worker").label(e.profile.firstName));
            }
        }
    }
}
