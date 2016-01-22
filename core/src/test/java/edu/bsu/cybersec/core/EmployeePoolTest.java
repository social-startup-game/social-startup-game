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

import com.google.common.collect.Sets;
import edu.bsu.cybersec.core.ui.GameAssets;
import org.junit.Test;
import playn.core.Image;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class EmployeePoolTest {

    private static final int TIMES_TO_RUN_PROBABLISTIC_TEST = 50;

    private EmployeePool pool;

    @Test
    public void testBossIsNotAnEmployee() {
        for (int i = 0; i < TIMES_TO_RUN_PROBABLISTIC_TEST; i++) {
            givenAFreshEmployeePool();
            Employee boss = pool.removeOne();
            Set<Employee> recruits = pool.recruit(3);
            assertFalse(recruits.contains(boss));
        }
    }

    @Test
    public void testRecruitCannotYieldRepeatResults() {
        for (int i = 0; i < TIMES_TO_RUN_PROBABLISTIC_TEST; i++) {
            givenAFreshEmployeePool();
            Set<Employee> recruits = Sets.newHashSet();
            while (!pool.isEmpty()) {
                Employee newRecruit = pool.recruit(1).iterator().next();
                assertFalse(recruits.contains(newRecruit));
                recruits.add(newRecruit);
            }
        }
    }

    private void givenAFreshEmployeePool() {
        GameAssets assets = mock(GameAssets.class);
        when(assets.getImage(any(GameAssets.ImageKey.class))).thenReturn(mock(Image.class));
        pool = EmployeePool.create(assets);
    }
}
