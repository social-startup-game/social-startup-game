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

package edu.bsu.cybersec.core.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.Company;
import edu.bsu.cybersec.core.EmployeePool;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.study.ConsentScreen;
import playn.core.Game;
import playn.core.Image;
import playn.core.Sound;
import playn.core.Tile;
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

import static com.google.common.base.Preconditions.*;

public class LoadingScreen extends ScreenStack.UIScreen {

    private final ScreenStack screenStack;
    private int assetTypesYetToCache = 3; // images, tiles, music
    private ProgressBar progressBar;

    public LoadingScreen(ScreenStack screenStack) {
        super(SimGame.game.plat);
        this.screenStack = checkNotNull(screenStack);
    }

    @Override
    public void wasShown() {
        super.wasShown();
        createUI();
        initializeProgressBar();
        startLoadingImages();
        startLoadingTiles();
        startLoadingSounds();
    }

    private void initializeProgressBar() {
        final int max =
                GameAssets.ImageKey.values().length
                        + GameAssets.TileKey.values().length
                        + MusicCache.instance().all().size()
                        + SfxCache.instance().all().size();
        final float width = size().width();
        final float height = size().height();
        progressBar = new ProgressBar(max, width * 0.55f, height * 0.02f);
        layer.addCenterAt(progressBar, width / 2, height * 3 / 5);
    }

    private void startLoadingImages() {
        List<RFuture<Image>> futures = SimGame.game.assets.cache(GameAssets.ImageKey.values());
        for (RFuture<Image> future : futures) {
            future.onSuccess(new Slot<Image>() {
                @Override
                public void onEmit(Image image) {
                    progressBar.increment();
                }
            });
            future.onFailure(new Slot<Throwable>() {
                @Override
                public void onEmit(Throwable throwable) {
                    SimGame.game.plat.log().error("Failed to load image", throwable);
                }
            });
        }
        RFuture.collect(futures).onSuccess(new Slot<Collection<Image>>() {
            @Override
            public void onEmit(Collection<Image> tiles) {
                countDown();
            }
        });
    }

    private void startLoadingTiles() {
        List<RFuture<Tile>> futures = SimGame.game.assets.cache(GameAssets.TileKey.values());
        for (RFuture<Tile> future : futures) {
            future.onSuccess(new Slot<Tile>() {
                @Override
                public void onEmit(Tile tile) {
                    progressBar.increment();
                }
            });
            future.onFailure(new Slot<Throwable>() {
                @Override
                public void onEmit(Throwable throwable) {
                    SimGame.game.plat.log().error("Failed to load tile", throwable);
                }
            });
        }
        RFuture.collect(futures).onSuccess(new Slot<Collection<Tile>>() {
            @Override
            public void onEmit(Collection<Tile> tiles) {
                countDown();
            }
        });
    }

    private void startLoadingSounds() {
        List<RFuture<Sound>> sounds = Lists.newArrayList();
        ImmutableList.Builder<Sound> allSoundsBuilder = new ImmutableList.Builder<>();
        allSoundsBuilder.addAll(MusicCache.instance().all());
        allSoundsBuilder.addAll(SfxCache.instance().all());
        for (Sound sound : allSoundsBuilder.build()) {
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
                    game().plat.log().warn("Failed to load a sound: " + event);
                } else {
                    countDown();
                }
            }
        });
    }

    private void countDown() {
        assetTypesYetToCache--;
        if (assetTypesYetToCache == 0) {
            startGame();
        }
    }

    private void startGame() {
        SimGame game = (SimGame) game();
        if (game.config.showConsentForm()) {
            screenStack.replace(new ConsentScreen(game), screenStack.slide());
        } else if (game.config.skipIntro()) {
            Company company = Company.from(EmployeePool.create(SimGame.game.assets)).withEmployees(3);
            screenStack.replace(new GameScreen(screenStack, company),
                    screenStack.slide());
        } else {
            screenStack.replace(new StartingScreen(screenStack), screenStack.slide());
        }
    }

    private void createUI() {
        Root root = iface.createRoot(AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics()), layer)
                .setSize(size());
        root.add(new Label("Loading...").addStyles(Style.COLOR.is(Palette.FOREGROUND)));
        root.addStyles(Style.BACKGROUND.is(Background.solid(Palette.BACKGROUND)));
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
