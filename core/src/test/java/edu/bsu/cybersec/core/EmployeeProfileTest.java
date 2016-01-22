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

import com.google.common.testing.EqualsTester;
import org.junit.Test;

public class EmployeeProfileTest {

    @Test
    public void testEquals() {
        EmployeeProfile bobRoss = EmployeeProfile.firstName("Bob").lastName("Ross").bio("Painter of happy little trees");
        EmployeeProfile otherBobRoss = EmployeeProfile.firstName("Bob").lastName("Ross").bio("Painter of happy little trees");
        EmployeeProfile billWatterson = EmployeeProfile.firstName("Bill").lastName("Watterson").bio("Comic creator extraordinaire");
        EqualsTester equalsTester = new EqualsTester().addEqualityGroup(bobRoss, otherBobRoss)
                .addEqualityGroup(billWatterson)
                .testEquals();
    }
}
