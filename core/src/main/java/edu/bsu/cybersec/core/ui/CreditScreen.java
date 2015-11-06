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
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.BorderLayout;
import tripleplay.util.Colors;

public class CreditScreen extends ScreenStack.UIScreen {
    private final ScreenStack screenStack;
    private final ScreenStack.UIScreen creditScreen = this;

    public CreditScreen(ScreenStack screenStack) {
        this.screenStack = screenStack;
        new Pointer(game().plat, layer, true);
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, new BorderLayout(), SimGameStyle.newSheet(game().plat.graphics())).setSize(size());
        Group labelGroup = createLabelGroup();
        root.add(createBackButton().setConstraint(BorderLayout.NORTH))
                .add(labelGroup.setConstraint(BorderLayout.CENTER))
                .setStyles(Style.BACKGROUND.is(Background.solid(Palette.START_BACKGROUND)));
        return root;
    }

    private Element createBackButton() {
        return new Group(new BorderLayout()).add(new Button("Back").onClick(new Slot<Button>() {

            @Override
            public void onEmit(Button button) {
                screenStack.remove(creditScreen, screenStack.slide().right());
            }
        }).setConstraint(BorderLayout.WEST));
    }

    private Group createLabelGroup() {
        return new Group(AxisLayout.vertical())
                .add(new Label("Developer:\t Paul Gestwicki").setStyles(Style.COLOR.is(Colors.WHITE)))
                .add(new Label("Developer:\t Kaleb Stumbaugh").setStyles(Style.COLOR.is(Colors.WHITE)))
                .add(new Label("Artist: \t Coy Yuan").setStyles(Style.COLOR.is(Colors.WHITE)))
                .add(new Label("Composer: \t Kevin MacLeod").setStyles(Style.COLOR.is(Colors.WHITE)));
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
