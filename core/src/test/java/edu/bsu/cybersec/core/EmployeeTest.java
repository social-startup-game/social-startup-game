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

import com.google.common.testing.EqualsTester;
import org.junit.Test;
import playn.core.Image;

import static org.mockito.Mockito.mock;

public class EmployeeTest {

    @Test
    public void testMocksEquality_differentMocksAreUnequal() {
        Object object1 = mock(Object.class);
        Object object2 = mock(Object.class);
        new EqualsTester()
                .addEqualityGroup(object1)
                .addEqualityGroup(object2)
                .testEquals();
    }

    @Test
    public void testEquals() {
        Employee e1 = makeEmployee();
        Employee e2 = makeEmployee();
        new EqualsTester()
                .addEqualityGroup(e1)
                .addEqualityGroup(e2)
                .testEquals();
    }

    private Employee makeEmployee() {
        return new Employee(mock(EmployeeProfile.class), mock(Image.class));
    }


}
