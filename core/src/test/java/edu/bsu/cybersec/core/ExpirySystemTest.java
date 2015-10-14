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

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class ExpirySystemTest extends AbstractSystemTest {

    private ExpirySystem system;

    @Override
    public void setUp() {
        super.setUp();
        system = new ExpirySystem(world);
    }

    @Test
    public void testExpiredItemIsRemoved() {
        Entity item = makeItemExpiringIn(0);
        advancePlayNClockOneMillisecond();
        assertTrue(item.isDisposed());
    }

    private Entity makeItemExpiringIn(int ms) {
        Entity item = world.create(true)
                .add(world.expiresIn);
        world.expiresIn.set(item.id, ms);
        return item;
    }

    @Test
    public void testUnexpiredItemIsNotRemoved() {
        Entity item = makeItemExpiringIn(ClockUtils.MS_PER_DAY);
        advancePlayNClockOneMillisecond();
        assertFalse(item.isDisposed());
    }

}
