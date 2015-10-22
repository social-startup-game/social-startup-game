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

import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Game;
import playn.core.Image;
import react.RFuture;
import react.Slot;
import react.Try;
import tripleplay.game.ScreenStack;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoadingScreen extends ScreenStack.UIScreen {

    private final ScreenStack screenStack;

    public LoadingScreen(ScreenStack screenStack) {
        this.screenStack = checkNotNull(screenStack);
    }

    @Override
    public void wasShown() {
        super.wasShown();
        List<RFuture<Image>> futures = Lists.newArrayList();
        for (Image image : ImageCache.instance().all()) {
            futures.add(image.state);
        }
        RFuture.collect(futures).onComplete(new Slot<Try<Collection<Image>>>() {
            @Override
            public void onEmit(Try<Collection<Image>> event) {
                if (event instanceof Try.Failure) {
                    game().plat.log().warn("Failed to load some images: " + event);
                } else {
                    screenStack.push(new GameScreen(), screenStack.slide());
                }
            }
        });
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics())).setSize(size());
        root.add(new Label("Loading...").addStyles(Style.COLOR.is(Colors.WHITE)));
        return root;
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
