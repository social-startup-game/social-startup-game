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

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class CompanyTest {

    private EmployeePool employeePool;
    private Company company;

    @Before
    public void setUp() {
        employeePool = mock(EmployeePool.class);
        when(employeePool.recruit(anyInt())).thenReturn(setOf(3));
        when(employeePool.removeOne()).thenReturn(mock(Employee.class));
    }

    private Set<Employee> setOf(int value) {
        checkArgument(value >= 0);
        Set<Employee> set = new HashSet<>();
        for (; value > 0; value--) {
            set.add(mock(Employee.class));
        }
        return set;
    }

    @Test
    public void testNumberOfEmployees() {
        whenACompanyIsCreated();
        assertEquals(3, company.employees.size());
    }

    private void whenACompanyIsCreated() {
        company = Company.from(employeePool).withEmployees(3);
    }

    @Test
    public void testBossIsNotAnEmployee() {
        whenACompanyIsCreated();
        assertFalse(company.employees.contains(company.boss));
    }
}
