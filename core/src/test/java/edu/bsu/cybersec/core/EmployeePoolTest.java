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

    @Test
    public void testBossIsNotAnEmployee() {
        for (int i = 0; i < TIMES_TO_RUN_PROBABLISTIC_TEST; i++) {
            GameAssets assets = mock(GameAssets.class);
            when(assets.getImage(any(GameAssets.ImageKey.class))).thenReturn(mock(Image.class));
            EmployeePool pool = EmployeePool.create(assets);
            Employee boss = pool.removeOne();
            Set<Employee> recruits = pool.recruit(3);
            assertFalse(recruits.contains(boss));
        }
    }
}
