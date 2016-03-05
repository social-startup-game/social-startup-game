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

package edu.bsu.cybersec.core.intro;

import edu.bsu.cybersec.core.Company;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.ui.GameScreen;
import edu.bsu.cybersec.core.ui.SimGameStyle;
import playn.core.Game;
import playn.core.Pointer;
import react.Connection;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

public class IntroScreen extends ScreenStack.UIScreen {

    private final ScreenStack screenStack;
    private final Slide slide;
    private final Company company;
    private Connection connection;

    public IntroScreen(ScreenStack screenStack, Slide slide, Company company) {
        super(SimGame.game.plat);
        this.screenStack = checkNotNull(screenStack);
        this.slide = checkNotNull(slide);
        this.company = checkNotNull(company);
    }

    @Override
    public void wasShown() {
        super.wasShown();
        createUI();
    }

    @Override
    public void showTransitionCompleted() {
        super.showTransitionCompleted();
        connection = SimGame.game.pointer.events.connect(new Slot<Pointer.Event>() {
            @Override
            public void onEmit(Pointer.Event event) {
                if (event.kind == Pointer.Event.Kind.END) {
                    connection.close();
                    advance();
                }
            }
        });
    }

    private void createUI() {
        Root root = iface.createRoot(AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics()), layer)
                .setSize(size());
        Group display = slide.createUI()
                .setConstraint(Constraints.fixedSize(SimGame.game.bounds.width(), SimGame.game.bounds.height()));
        root.add(display);
        root.addStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
    }

    private void advance() {
        if (slide.hasNext()) {
            screenStack.replace(new IntroScreen(screenStack, slide.next(), company), screenStack.slide());
        } else {
            screenStack.replace(new GameScreen(screenStack, company), screenStack.slide());
        }
    }

    @Override
    public Game game() {
        return SimGame.game;
    }

}
