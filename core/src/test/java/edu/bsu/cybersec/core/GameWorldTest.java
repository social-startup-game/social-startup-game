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
