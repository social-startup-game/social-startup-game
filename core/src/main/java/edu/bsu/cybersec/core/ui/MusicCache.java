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

import com.google.common.collect.ImmutableList;
import playn.core.Assets;
import playn.core.Sound;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public abstract class MusicCache {
    private static MusicCache instance;

    public final Sound GAME_THEME = load("Pamgaea");
    private final ImmutableList<Sound> all = ImmutableList.of(GAME_THEME);

    public static MusicCache initialize(final Assets assets) {
        if (instance == null) {
            return instance = new MusicCache() {
                @Override
                protected Sound load(String name) {
                    Sound sound = assets.getMusic("music/" + name);
                    sound.setLooping(true);
                    return sound;
                }
            };
        } else {
            return instance;
        }
    }

    public List<Sound> all() {
        return all;
    }

    public static MusicCache instance() {
        checkState(instance != null, "Must be initialized first");
        return instance;
    }

    protected abstract Sound load(String name);
}
