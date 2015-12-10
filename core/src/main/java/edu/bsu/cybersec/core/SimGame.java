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

import edu.bsu.cybersec.core.ui.*;
import playn.core.Platform;
import playn.scene.SceneGame;
import pythagoras.f.IRectangle;
import tripleplay.game.ScreenStack;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimGame extends SceneGame {

    private static final float IPHONE5_VERTICAL_ASPECT_RATIO = 9f / 16f;

    // This is required for ScreenStack.UIScreen's game() method, which is called before the constructor.
    // See https://github.com/threerings/tripleplay/blob/master/demo/core/src/main/java/tripleplay/demo/TripleDemo.java#L19
    public static SimGame game;

    public final GameConfig config;

    public final GameAssets assets;

    public final GameBounds bounds;

    public SimGame(Platform plat, GameConfig config) {
        super(plat, 33);
        assets = new GameAssets(plat.assets());
        game = this;
        this.config = checkNotNull(config);
        IRectangle box = new AspectRatioTool(IPHONE5_VERTICAL_ASPECT_RATIO)
                .createBoundingBoxWithin(plat.graphics().viewSize);
        this.bounds = new GameBounds(box);
        handleMusicMute();
        initializeAssetCaches();
        pushFirstScreen();
    }

    private void handleMusicMute() {
        if (config.muteMusic()) {
            Jukebox.instance().muted.update(true);
        }
    }

    private void initializeAssetCaches() {
        MusicCache.initialize(plat.assets());
        FontCache.initialize(plat.graphics());
    }

    private void pushFirstScreen() {
        ScreenStack screenStack = new ScreenStack(this, rootLayer);
        screenStack.push(new LoadingScreen(screenStack));
    }
}