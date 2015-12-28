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

package edu.bsu.cybersec.core.narrative;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import tripleplay.entity.Entity;

import static org.junit.Assert.assertTrue;

public class DefaultNarrativeScriptTestConfigurationTest extends AbstractDefaultNarrativeScriptTest {

    private static final int EXPECTED_MINIMUM_NUMBER_OF_EVENTS = 4;

    @Test
    public void testTestingConfiguration_testCanReadEventsFromGameWorld() {
        whenTheNarrativeScriptIsCreated();
        int count = countWhatWeBelieveToBeNarrativeEvents();
        assertTrue("Expected at least " + EXPECTED_MINIMUM_NUMBER_OF_EVENTS + " but found " + count,
                count >= EXPECTED_MINIMUM_NUMBER_OF_EVENTS);
    }


    private int countWhatWeBelieveToBeNarrativeEvents() {
        int count = 0;
        for (Entity entity : ImmutableList.copyOf(world.entities())) {
            if (isNarrativeEvent(entity)) {
                count++;
            }
        }
        return count;
    }
}


