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
import playn.core.Clock;

public abstract class AbstractSystemTest {

    protected static final float EPSILON = 0.00001f;

    protected GameWorld world;
    private PlayNClockUtil clockUtil;

    @Before
    public void setUp() {
        world = new GameWorld();
        clockUtil = new PlayNClockUtil();
    }


    protected void advanceOneMillisecond() {
        clockUtil.advance(1);
    }

    protected void advanceOneSecond() {
        clockUtil.advance(1000);
    }

    protected void advanceOneHour() {
        clockUtil.advance(ClockUtils.MS_PER_HOUR);
    }

    protected void advanceOneDay() {
        clockUtil.advance(ClockUtils.MS_PER_DAY);
    }

    private final class PlayNClockUtil {

        public void advance(int ms) {
            Clock playnClock = new Clock();
            playnClock.dt = ms;
            playnClock.tick += ms;
            world.update(playnClock);
        }
    }

}
