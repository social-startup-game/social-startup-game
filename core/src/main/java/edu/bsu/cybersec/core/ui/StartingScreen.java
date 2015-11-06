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

import edu.bsu.cybersec.core.SimGame;
import playn.core.Game;
import playn.core.Image;
import playn.scene.Pointer;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.checkNotNull;

public class StartingScreen extends ScreenStack.UIScreen {
    private final ScreenStack screenStack;

    public StartingScreen(ScreenStack screenStack) {
        this.screenStack = checkNotNull(screenStack);
        new Pointer(game().plat, layer, true);
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics())).setSize(size());
        Image logo = ImageCache.instance().LOGO;
        Icon iconLogo = Icons.image(logo.tile());
        root.add(new Label(iconLogo))
                .add(new Button("Start the Game!").onClick(new Slot<Button>() {
                    @Override
                    public void onEmit(Button button) {
                        screenStack.push(new GameScreen(screenStack), screenStack.slide());
                    }
                }));
        root.setStyles(Style.BACKGROUND.is(Background.solid(Palette.START_BACKGROUND)));
        return root;
    }


    @Override
    public Game game() {
        return SimGame.game;
    }

}
