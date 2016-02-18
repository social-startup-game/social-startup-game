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

import edu.bsu.cybersec.core.*;
import playn.core.Clock;
import playn.core.Game;
import playn.scene.Layer;
import playn.scene.Mouse;
import playn.scene.Pointer;
import pythagoras.f.IDimension;
import react.UnitSlot;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Layers;

import static com.google.common.base.Preconditions.checkNotNull;

public class GameScreen extends ScreenStack.UIScreen {
    private final GameWorld.Systematized gameWorld;

    private final Label timeLabel = new Label("");
    private final Label usersLabel = new Label("");

    private final ScreenStack screenStack;

    public GameScreen(final ScreenStack screenStack, Company company) {
        super(SimGame.game.plat);

        this.screenStack = checkNotNull(screenStack);
        new Pointer(game().plat, layer, true);
        game().plat.input().mouseEvents.connect(new Mouse.Dispatcher(layer, true));

        gameWorld = createGameWorld(company);
        initializeTimeRenderingSystem();
        initializeHudRenderingSystem();

        gameWorld.connect(update, paint);
        game().plat.input().keyboardEvents.connect(new DebugMode(gameWorld));
    }

    private GameWorld.Systematized createGameWorld(final Company company) {
        PlayableWorldFactory playableWorldFactory = new PlayableWorldFactory(SimGame.game.config, company);
        final GameWorld.Systematized product = playableWorldFactory.createPlayableGameWorld();
        product.onGameEnd.connect(new UnitSlot() {
            @Override
            public void onEmit() {
                screenStack.replace(new EndScreen(screenStack, product, company), screenStack.slide());
            }
        });
        return product;
    }

    private void initializeTimeRenderingSystem() {
        new System(gameWorld, SystemPriority.UI_LEVEL.value) {
            {
                Entity updater = gameWorld.create(true).add(gameWorld.onUpdate);
                gameWorld.onUpdate.set(updater.id, new Updatable() {
                    @Override
                    public void update(Clock clock) {
                        final long tick = gameWorld.gameTime.get().now;
                        now = gameWorld.startTime + tick;
                        final int day = computeInGameDay();
                        String text = "Day " + day + " " + formatter.format(now * (long) ClockUtils.MS_PER_SECOND);
                        timeLabel.text.update(text);
                    }

                    private int computeInGameDay() {
                        return ((gameWorld.gameTime.get().now + GameWorld.START_HOUR_OFFSET)
                                / ClockUtils.SECONDS_PER_DAY) + 1;
                    }
                });
            }

            private final PlatformSpecificDateFormatter formatter =
                    ((SimGame) game()).config.dateFormatter();

            private long now = gameWorld.startTime;

            @Override
            protected boolean isInterested(Entity entity) {
                return false;
            }
        };
    }

    private void initializeHudRenderingSystem() {
        new System(gameWorld, SystemPriority.UI_LEVEL.value) {
            @Override
            protected boolean isInterested(Entity entity) {
                return true;
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                final float numberOfUsers = gameWorld.users.get();
                usersLabel.text.update("Users: " + (int) numberOfUsers);
            }
        };
    }

    @Override
    public void wasShown() {
        super.wasShown();
        createUI();
        Jukebox.instance().loop(MusicCache.instance().GAME_THEME);
    }

    private void createUI() {
        IDimension viewSize = game().plat.graphics().viewSize;
        GameBounds contentBounds = SimGame.game.bounds;
        Root root = iface.createRoot(new AbsoluteLayout(), makeStyleSheet(), layer);
        Group content = createContentGroup(root);
        root.add(AbsoluteLayout.at(content,
                contentBounds.x(), contentBounds.y(), contentBounds.width(), contentBounds.height()))
                .addStyles(Style.BACKGROUND.is(Background.solid(Palette.UNUSED_SPACE)))
                .setSize(size());
        if (viewSize.width() > contentBounds.width()) {
            createLeftSideLayerToCoverShiftingEmployees((viewSize.width() - contentBounds.width()) / 2, viewSize.height());
        }
    }

    private Group createContentGroup(Root root) {
        final Group content = new Group(AxisLayout.vertical().gap(0).offStretch());
        content.add(new TopStatusBar()
                .setConstraint(Constraints.fixedHeight(30)));
        content.add(new MainUIGroup(gameWorld, iface, root)
                .setConstraint(AxisLayout.stretched()));
        return content;
    }

    private Stylesheet makeStyleSheet() {
        Stylesheet.Builder builder = SimGameStyle.newSheetBuilder(game().plat.graphics());
        builder.add(Label.class, Style.COLOR.is(Palette.FOREGROUND));
        return builder.create();
    }

    private void createLeftSideLayerToCoverShiftingEmployees(float width, float height) {
        Layer sideLayer = Layers.solid(Palette.UNUSED_SPACE, width, height);
        layer.addAt(sideLayer, 0, 0);
    }

    @Override
    public Game game() {
        return SimGame.game;
    }

    private final class TopStatusBar extends Group {

        private final float SHIM_WIDTH = SimGame.game.bounds.percentOfHeight(0.05f);

        public TopStatusBar() {
            super(AxisLayout.horizontal());
            add(new Shim(0, 0).setConstraint(Constraints.fixedSize(SHIM_WIDTH, 0)));
            add(timeLabel.addStyles(Style.HALIGN.left).setConstraint(AxisLayout.stretched()));
            add(new Shim(0, 0).setConstraint(Constraints.fixedSize(SHIM_WIDTH, 0)));
            add(usersLabel.addStyles(Style.HALIGN.left).setConstraint(AxisLayout.stretched()));
            addStyles(Style.BACKGROUND.is(Background.solid(Palette.BACKGROUND)));
        }
    }

}
