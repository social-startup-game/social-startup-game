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
import edu.bsu.cybersec.core.ClockUtils;
import edu.bsu.cybersec.core.WorkHoursPredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import tripleplay.entity.Entity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test for {@link DefaultNarrativeScript}.
 * <p/>
 * This usesthe approach described
 * on <a href="http://stackoverflow.com/questions/1492856/easy-way-of-running-the-same-junit-test-over-and-over">
 * StackOverflow</a>, leveraging {@link org.junit.runners.Parameterized.Parameter} to run the same test
 * several times. The events are randomly placed, and so we run the item multiple times to gain more
 * confidence in the results.
 */
@RunWith(Parameterized.class)
public class DefaultNarrativeScriptTest extends AbstractDefaultNarrativeScriptTest {

    private static final int TIMES_TO_RUN_TEST = 5;

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[TIMES_TO_RUN_TEST][0]);
    }

    @Test
    public void testCreate_noEventsOutsideWorkHours() {
        whenTheNarrativeScriptIsCreated();
        WorkHoursPredicate pred = WorkHoursPredicate.instance();
        for (Entity entity : ImmutableList.copyOf(world.entities())) {
            if (isNarrativeEvent(entity)) {
                int time = world.timeTrigger.get(entity.id);
                assertTrue("Time " + time + " (hour " + (time / ClockUtils.SECONDS_PER_HOUR) +
                        ") is outside work hours.", pred.apply(time));
            }
        }
    }
}
