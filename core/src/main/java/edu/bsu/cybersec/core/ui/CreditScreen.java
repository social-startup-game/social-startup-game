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
import playn.scene.Pointer;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

public class CreditScreen extends ScreenStack.UIScreen {
    private static final String[] CREDITS = new String[]{
            "Game Design and Development:",
            "Paul Gestwicki and Kaleb Stumbaugh",
            " ",
            "Visuals:",
            "Coy Yuan",
            " ",
            "Music:",
            "Chipper Doodle and Pamgaea",
            "by Kevin MacLeod (incompetech.com)",
            "Licensed under Creative Commons: By Attribution 3.0",
            "http://creativecommons.org/licenses/by/3.0/"
    };

    private final ScreenStack screenStack;
    private final ScreenStack.UIScreen creditScreen = this;

    public CreditScreen(ScreenStack screenStack) {
        super(SimGame.game.plat);
        this.screenStack = screenStack;
        new Pointer(game().plat, layer, true);
    }

    @Override
    public void wasShown() {
        super.wasShown();
        createUI();
    }

    private void createUI() {
        Root root = iface.createRoot(AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics()), layer)
                .setSize(size());
        root.add(createContentArea())
                .setStyles(Style.BACKGROUND.is(Background.solid(Palette.START_BACKGROUND)));
    }

    private Group createContentArea() {
        Group group = new Group(AxisLayout.vertical());
        group.add(new Label(Icons.image(SimGame.game.assets.getImage(GameAssets.ImageKey.LOGO))));
        for (String s : CREDITS) {
            group.add(new Label(s)
                    .setStyles(
                            Style.COLOR.is(Colors.WHITE),
                            Style.TEXT_WRAP.on));
        }
        group.add(new Label(" "));
        group.add(createBackButton());
        return group;
    }

    private Button createBackButton() {
        return new Button("OK").onClick(new Slot<Button>() {
            @Override
            public void onEmit(Button button) {
                screenStack.remove(creditScreen, screenStack.slide().right());
            }
        });
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
