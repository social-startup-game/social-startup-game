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

package edu.bsu.cybersec.core.ui;

import org.junit.Before;
import org.junit.Test;
import playn.core.Sound;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public final class JukeboxTest {

    private Sound sound;
    private Jukebox jukebox;

    @Before
    public void setUp() {
        sound = mock(Sound.class);
        jukebox = new Jukebox();
    }

    @Test
    public void test_startsUnmuted() {
        assertFalse(jukebox.muted.get());
    }

    @Test
    public void testLoop_playsSong() {
        jukebox.loop(sound);
        verify(sound).play();
    }

    @Test
    public void testLoop_muted_doesNotPlaySong() {
        jukebox.mute();
        jukebox.loop(sound);
        verify(sound, times(0)).play();
    }

    @Test
    public void testUnmute_whilePlaying_songIsPlayed() {
        jukebox.mute();
        jukebox.loop(sound);
        jukebox.unmute();
        verify(sound).play();
    }

    @Test
    public void testMute_whilePlaying_songIsStopped() {
        jukebox.loop(sound);
        jukebox.mute();
        verify(sound).stop();
    }
}
