/*
 * Copyright 2016 Paul Gestwicki
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

package edu.bsu.cybersec.java;

import org.junit.Test;
import playn.core.Log;
import playn.java.JavaPlatform;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class LogCollectorTest {

    private static final String MESSAGE = "A test message";

    private Log.Collector collector;
    private JavaPlatform plat;

    @Test
    public void testPlayNLogCollector() {
        givenAHeadlessPlatformWithALogCollector();
        whenAMessageIsLoggedAtInfoLevel();
        thenTheCollectorIsNotified();
    }

    private void givenAHeadlessPlatformWithALogCollector() {
        collector = mock(Log.Collector.class);
        plat = new JavaPlatform.Headless(new JavaPlatform.Headless.Config());
        plat.log().setCollector(collector);
    }

    private void whenAMessageIsLoggedAtInfoLevel() {
        plat.log().info(MESSAGE);
    }

    private void thenTheCollectorIsNotified() {
        verify(collector).logged(Log.Level.INFO, MESSAGE, null);
    }
}
