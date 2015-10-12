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
import tripleplay.entity.Component;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GameWorldTest {

    private GameWorld world;

    @Before
    public void setUp() {
        world = new GameWorld();
    }

    @Test
    public void testAllComponentsHaveTheProperNames() {
        for (String name : world.components.keySet()) {
            Component component = world.components.get(name);
            try {
                Field field = GameWorld.class.getField(name);
                assertTrue("The field named " + name + " does not correspond to the mapped component.",
                        component == field.get(world));
            } catch (NoSuchFieldException e) {
                fail("There is no field named " + name);
            } catch (IllegalAccessException e) {
                reportReflectionFailure();
            }
        }
    }

    private void reportReflectionFailure() {
        fail("Cannot access field for reflection-based test.");
    }

    @Test
    public void testAllComponentsHaveAMapping() {
        for (Field f : GameWorld.class.getFields()) {
            if (Component.class.isAssignableFrom(f.getType())) {
                try {
                    Component component = (Component) f.get(world);
                    assertTrue("Missing component mapping for " + f.getName(),
                            world.components.containsValue(component));
                } catch (IllegalAccessException e) {
                    reportReflectionFailure();
                }
            }
        }
    }
}
