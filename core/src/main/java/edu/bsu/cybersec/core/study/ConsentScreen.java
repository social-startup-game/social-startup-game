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

package edu.bsu.cybersec.core.study;

import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.ui.GameColors;
import edu.bsu.cybersec.core.ui.SimGameStyle;
import edu.bsu.cybersec.core.ui.StartingScreen;
import playn.core.Game;
import playn.scene.Pointer;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

public class ConsentScreen extends ScreenStack.UIScreen {

    private static final int BG_COLOR = GameColors.GRANNY_SMITH;

    private final SimGame game;
    private Label label = new Label("Loading consent form...");

    public ConsentScreen(final SimGame game) {
        super(game.plat);
        this.game = game;
        new Pointer(game().plat, layer, true);
        game.plat.assets().getText("text/consent.txt").onSuccess(new Slot<String>() {
            @Override
            public void onEmit(String s) {
                label.text.update(s);
            }
        });
        iface.createRoot(AxisLayout.vertical().offStretch(), SimGameStyle.newSheet(game.plat.graphics()), layer)
                .setSize(game.bounds.width(), game.bounds.height())
                .addStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE)))
                .add(new Label("Informed Consent Form")
                                .addStyles(
                                        Style.BACKGROUND.is(
                                                Background.solid(BG_COLOR).inset(0, game.bounds.percentOfHeight(0.02f)))),
                        new Scroller(label.addStyles(
                                Style.COLOR.is(Colors.BLACK),
                                Style.TEXT_WRAP.on,
                                Style.HALIGN.left))
                                .setBehavior(Scroller.Behavior.VERTICAL)
                                .setConstraint(AxisLayout.stretched()),
                        new Group(AxisLayout.vertical().offStretch())
                                .addStyles(Style.BACKGROUND.is(Background.solid(BG_COLOR)
                                        .inset(0, game.bounds.percentOfHeight(0.02f))))
                                .add(new Button("Grant informed consent and play the game")
                                                .onClick(new Slot<Button>() {
                                                    @Override
                                                    public void onEmit(Button button) {
                                                        game.config.enableGameplayLogging();
                                                        game.screenStack.replace(new PreGameSurveyScreen(game), game.screenStack.slide());
                                                    }
                                                }),
                                        new Button("Play without giving informed consent")
                                                .onClick(new Slot<Button>() {
                                                    @Override
                                                    public void onEmit(Button button) {
                                                        game.screenStack.replace(new StartingScreen(game.screenStack), game.screenStack.slide());
                                                    }
                                                })))
                .setLocation((game.plat.graphics().viewSize.width() - game.bounds.width()) / 2,
                        (game.plat.graphics().viewSize.height() - game.bounds.height()) / 2);
    }

    @Override
    public Game game() {
        return game;
    }
}
