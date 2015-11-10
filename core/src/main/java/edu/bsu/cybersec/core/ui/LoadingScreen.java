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
import playn.core.Sound;
import react.RFuture;
import react.Slot;
import react.Try;
import tripleplay.game.ScreenStack;
import tripleplay.ui.Background;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoadingScreen extends ScreenStack.UIScreen {

    private final ScreenStack screenStack;
    private int countdown = 2;
    private ProgressBar progressBar;

    public LoadingScreen(ScreenStack screenStack) {
        this.screenStack = checkNotNull(screenStack);
    }

    @Override
    public void wasShown() {
        super.wasShown();
        initializeProgressBar();
        startLoadingImages();
        startLoadingSounds();
    }

    private void initializeProgressBar() {
        final int max = ImageCache.instance().all().size()
                + MusicCache.instance().all().size();
        final float width = size().width();
        final float height = size().height();
        progressBar = new ProgressBar(max, width * 0.55f, height * 0.02f);
        layer.addCenterAt(progressBar, width / 2, height * 3 / 5);
    }

    private void startLoadingImages() {
        List<RFuture<Image>> futures = Lists.newArrayList();
        for (Image image : ImageCache.instance().all()) {
            futures.add(image.state);
            image.state.onComplete(new Slot<Try<Image>>() {
                @Override
                public void onEmit(Try<Image> event) {
                    progressBar.increment();
                }
            });
        }
        RFuture.collect(futures).onComplete(new Slot<Try<Collection<Image>>>() {
            @Override
            public void onEmit(Try<Collection<Image>> event) {
                if (event.isFailure()) {
                    game().plat.log().warn("Failed to load some images: " + event);
                } else {
                    countDown();
                }
            }
        });
    }

    private void startLoadingSounds() {
        List<RFuture<Sound>> sounds = Lists.newArrayList();
        for (Sound sound : MusicCache.instance().all()) {
            sounds.add(sound.state);
            sound.state.onComplete(new Slot<Try<Sound>>() {
                @Override
                public void onEmit(Try<Sound> event) {
                    progressBar.increment();
                }
            });
        }
        RFuture.collect(sounds).onComplete(new Slot<Try<Collection<Sound>>>() {
            @Override
            public void onEmit(Try<Collection<Sound>> event) {
                if (event.isFailure()) {
                    game().plat.log().warn("Failed to load some sound: " + event);
                } else {
                    countDown();
                }
            }
        });
    }

    private void countDown() {
        countdown--;
        if (countdown == 0) {
            startGame();
        }
    }

    private void startGame() {
        if (((SimGame) game()).config.skipIntro()) {
            screenStack.push(new GameScreen(screenStack), screenStack.slide());
        } else {
            screenStack.push(new StartingScreen(screenStack), screenStack.slide());
        }
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics())).setSize(size());
        root.add(new Label("Loading...").addStyles(Style.COLOR.is(Palette.FOREGROUND)));
        root.addStyles(Style.BACKGROUND.is(Background.solid(Palette.BACKGROUND)));
        return root;
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
