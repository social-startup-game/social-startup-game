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

    protected void advanceOneSecond() {
        clockUtil.advance(1000);
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
