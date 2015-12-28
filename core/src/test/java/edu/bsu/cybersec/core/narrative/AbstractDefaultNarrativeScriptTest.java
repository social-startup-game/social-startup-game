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

import edu.bsu.cybersec.core.ClockUtils;
import edu.bsu.cybersec.core.GameConfig;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import org.junit.Before;
import tripleplay.entity.Entity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractDefaultNarrativeScriptTest {

    private static final int SECONDS_PER_DAY = ClockUtils.SECONDS_PER_HOUR * 24;

    protected GameWorld.Systematized world;
    private GameConfig config;

    @Before
    public void setUp() {
        world = new GameWorld.Systematized();
        world.gameEnd.update(SECONDS_PER_DAY * 14);
        config = mock(GameConfig.class);
        when(config.useNarrativeEvents()).thenReturn(true);
    }

    protected void whenTheNarrativeScriptIsCreated() {
        new DefaultNarrativeScript().createIn(world, config);
    }

    protected boolean isNarrativeEvent(Entity entity) {
        return entity.has(world.event) && entity.has(world.timeTrigger)
                && world.event.get(entity.id) instanceof NarrativeEvent;
    }
}
