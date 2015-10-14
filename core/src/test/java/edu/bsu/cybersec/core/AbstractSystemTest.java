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

    protected void advancePlayNClockOneMillisecond() {
        clockUtil.advance(1);
    }

    protected void advancePlayNClockOneSecond() {
        clockUtil.advance(1000);
    }

    protected void advancePlayNClockOneHour() {
        clockUtil.advance(ClockUtils.MS_PER_HOUR);
    }

    protected void advancePlayNClockOneDay() {
        clockUtil.advance(ClockUtils.MS_PER_DAY);
    }

    protected void whenOneHourOfGameTimeElapses() {
        world.advanceGameTime(ClockUtils.MS_PER_HOUR);
        advancePlayNClockOneHour();
    }

    protected void whenOneDayOfGameTimeElapses() {
        world.advanceGameTime(ClockUtils.MS_PER_DAY);
        advancePlayNClockOneDay();
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
