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

package edu.bsu.cybersec.core.systems;

import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.*;

public final class UserAttritionSystemTest extends AbstractSystemTest {

    @Override
    public void setUp() {
        super.setUp();
        new UserAttritionSystem(world);
    }

    @Test
    public void testUpdate_oneExploit_loseUsersAtAttritionRatePerHour() {
        givenAWorldWithUsers(10);
        createAttritionSource(0.5f);
        whenOneHourOfGameTimeElapses();
        thenTheNumberOfUsersIs(5);
    }

    private void thenTheNumberOfUsersIs(int expected) {
        assertEquals(expected, world.users.get(), EPSILON);
    }

    private Entity createAttritionSource(float attrition) {
        Entity e = world.create(true).add(world.userAttrition, world.lostUsers);
        world.userAttrition.set(e.id, attrition);
        return e;
    }

    private void givenAWorldWithUsers(int numberOfUsers) {
        world.users.update((float) numberOfUsers);
    }

    @Test
    public void testUpdate_oneExploit_exploitStoresLostUsers() {
        givenAWorldWithUsers(10);
        Entity attritionSource = createAttritionSource(0.3f);
        whenOneHourOfGameTimeElapses();
        assertEquals(3, world.lostUsers.get(attritionSource.id), EPSILON);
    }

    @Test
    public void testUpdate_noGameTimeElapses_noAttrition() {
        givenAWorldWithUsers(100);
        createAttritionSource(0.5f);
        advancePlayNClockOneDay();
        thenTheNumberOfUsersIs(100);
    }
}
