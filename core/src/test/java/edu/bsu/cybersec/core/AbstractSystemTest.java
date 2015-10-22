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
    protected static final Task IDLE = Task.createTask("Idle").build();

    protected GameWorld world;
    private PlayNClockUtil clockUtil;

    @Before
    public void setUp() {
        world = new GameWorld();
        clockUtil = new PlayNClockUtil();
    }

    protected void advancePlayNClockOneMillisecond() {
        advancePlayNClock(1);
    }

    private void advancePlayNClock(int ms) {
        clockUtil.advance(ms);
    }

    protected void advancePlayNClockOneSecond() {
        advancePlayNClock(ClockUtils.MS_PER_SECOND);
    }

    protected void advancePlayNClockOneHour() {
        advancePlayNClock(ClockUtils.MS_PER_HOUR);
    }

    protected void advancePlayNClockOneDay() {
        advancePlayNClock(ClockUtils.MS_PER_DAY);
    }

    protected void whenOneHourOfGameTimeElapses() {
        whenGameTimeElapses(ClockUtils.MS_PER_HOUR);
    }

    protected void whenGameTimeElapses(int ms) {
        world.advanceGameTime(ms);
        advancePlayNClock(ms);
    }

    protected void whenOneDayOfGameTimeElapses() {
        whenGameTimeElapses(ClockUtils.MS_PER_DAY);
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
