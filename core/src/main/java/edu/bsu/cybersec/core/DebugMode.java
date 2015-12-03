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

import com.google.common.collect.ImmutableMap;
import playn.core.Key;
import playn.core.Keyboard;
import react.SignalView;

import static com.google.common.base.Preconditions.checkNotNull;

public class DebugMode implements SignalView.Listener<Keyboard.Event> {

    private final ImmutableMap<Key, ? extends Runnable> actionMap = ImmutableMap.of(
            Key.E, new Runnable() {
                @Override
                public void run() {
                    gameWorld.exploitSystem.addExploit();
                }
            });
    private final GameWorld.Systematized gameWorld;

    public DebugMode(GameWorld.Systematized gameWorld) {
        this.gameWorld = checkNotNull(gameWorld);
    }

    @Override
    public void onEmit(Keyboard.Event event) {
        if (event instanceof Keyboard.KeyEvent) {
            Keyboard.KeyEvent keyEvent = (Keyboard.KeyEvent) event;
            if (keyEvent.down && actionMap.containsKey(keyEvent.key)) {
                actionMap.get(keyEvent.key).run();
            }
        }
    }
}
