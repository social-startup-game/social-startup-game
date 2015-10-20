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

import edu.bsu.cybersec.core.ui.ImageCache;
import edu.bsu.cybersec.core.ui.LoadingScreen;
import playn.core.Platform;
import playn.scene.SceneGame;
import tripleplay.game.ScreenStack;

public class SimGame extends SceneGame {

    // This is required for ScreenStack.UIScreen's game() method, which is called before the constructor.
    // See https://github.com/threerings/tripleplay/blob/master/demo/core/src/main/java/tripleplay/demo/TripleDemo.java#L19
    public static SimGame game;

    public SimGame(Platform plat) {
        super(plat, 33);
        game = this;
        ImageCache.initialize(plat.assets());
        ScreenStack screenStack = new ScreenStack(this, rootLayer);
        screenStack.push(new LoadingScreen(screenStack));
    }

}