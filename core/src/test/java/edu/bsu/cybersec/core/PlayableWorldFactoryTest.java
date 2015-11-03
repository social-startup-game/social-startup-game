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

import com.google.common.collect.Range;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayableWorldFactoryTest extends AbstractMockedAssetsTest {

    private GameWorld.Systematized world;

    @Override
    public void setUp() {
        super.setUp();
        PlayableWorldFactory factory = new PlayableWorldFactory(imageCache);
        world = factory.createPlayableGameWorld();
    }

    @Test
    public void testCreate_workerListHasThreeWorkers() {
        assertEquals(3, world.workers.size());
    }

    @Test
    public void testCreate_exposureIsValidPercent() {
        Range<Float> range = Range.closed(0f, 1f);
        final float exposure = world.exposure.get();
        assertTrue("Exposure (" + exposure + ") is out of range.", range.contains(exposure));
    }
}
