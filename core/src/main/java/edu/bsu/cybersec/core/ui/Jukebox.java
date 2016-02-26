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

import com.google.common.collect.Maps;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Sound;
import react.Slot;
import react.Value;

import java.util.Map;

import static com.google.common.base.Preconditions.*;

public final class Jukebox {

    private static final Jukebox SINGLETON = new Jukebox();

    public static Jukebox instance() {
        return SINGLETON;
    }

    private Sound currentTrack;

    public final Value<Boolean> muted = Value.create(false);

    Jukebox() {
        muted.connect(new Slot<Boolean>() {
            @Override
            public void onEmit(Boolean muted) {
                if (!muted && currentTrack != null) {
                    currentTrack.play();
                } else if (muted && currentTrack != null) {
                    currentTrack.stop();
                }
            }
        });
        silenceSoundEffectsWhileMuted();
    }

    private void silenceSoundEffectsWhileMuted() {
        muted.connect(new Slot<Boolean>() {
            private final Map<Sound, Float> storedVolumes = Maps.newHashMap();

            @Override
            public void onEmit(Boolean muted) {
                if (muted) {
                    muteSounds();
                } else {
                    restoreVolumes();
                }
            }

            private void muteSounds() {
                for (Sound s : SfxCache.instance().all()) {
                    storedVolumes.put(s, s.volume());
                    s.setVolume(0);
                    SimGame.game.plat.log().debug("Set volume to zero");
                }
            }

            private void restoreVolumes() {
                for (Sound s : storedVolumes.keySet()) {
                    s.setVolume(storedVolumes.get(s));
                }
            }
        });
    }

    public void loop(Sound track) {
        checkNotNull(track);
        if (currentTrack == null) {
            startPlaying(track);
        } else if (!currentTrack.equals(track)) {
            currentTrack.stop();
            startPlaying(track);
        }
    }

    private void startPlaying(Sound track) {
        currentTrack = track;
        if (!muted.get()) {
            track.setLooping(true);
            track.play();
        }
    }

    public void toggleMute() {
        muted.update(!muted.get());
    }
}
