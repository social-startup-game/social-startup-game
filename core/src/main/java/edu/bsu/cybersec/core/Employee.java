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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import playn.core.Image;

import static com.google.common.base.Preconditions.checkNotNull;

public class Employee {
    public final EmployeeProfile profile;
    public final Image image;

    Employee(EmployeeProfile profile, Image image) {
        this.profile = checkNotNull(profile);
        this.image = checkNotNull(image);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("profile", profile)
                .add("image", image)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(profile, image);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Employee) {
            Employee other = (Employee) obj;
            return Objects.equal(this.profile, other.profile)
                    && Objects.equal(this.image, other.image);
        } else {
            return false;
        }
    }
}
