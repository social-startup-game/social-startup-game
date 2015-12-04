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

import edu.bsu.cybersec.core.*;
import playn.core.Clock;
import playn.core.Game;
import playn.scene.Mouse;
import playn.scene.Pointer;
import react.UnitSlot;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;

public class GameScreen extends ScreenStack.UIScreen {
    private final GameWorld.Systematized gameWorld;

    {
        PlayableWorldFactory playableWorldFactory = new PlayableWorldFactory(SimGame.game.assets, SimGame.game.config);
        gameWorld = playableWorldFactory.createPlayableGameWorld();
        gameWorld.onGameEnd.connect(new UnitSlot() {
            @Override
            public void onEmit() {
                screenStack.replace(new EndScreen(screenStack, gameWorld), screenStack.slide());
            }
        });
    }

    @SuppressWarnings("unused")
    private tripleplay.entity.System timeRenderingSystem = new System(gameWorld, SystemPriority.UI_LEVEL.value) {
        {
            Entity updater = gameWorld.create(true).add(gameWorld.onUpdate);
            gameWorld.onUpdate.set(updater.id, new Updatable() {
                @Override
                public void update(Clock clock) {
                    final long tick = gameWorld.gameTime.get().now;
                    now = startTime + tick;
                    final String formatted = formatter.format(now * (long) ClockUtils.MS_PER_SECOND);
                    timeLabel.text.update(formatted);
                }
            });
        }

        private final PlatformSpecificDateFormatter formatter =
                ((SimGame) game()).config.dateFormatter();

        private final long startTime = new java.util.Date().getTime() / ClockUtils.MS_PER_SECOND;
        private long now = startTime;

        @Override
        protected boolean isInterested(Entity entity) {
            return false;
        }
    };

    @SuppressWarnings("unused")
    tripleplay.entity.System hudRenderingSystem = new System(gameWorld, SystemPriority.UI_LEVEL.value) {
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

    private final Label timeLabel = new Label("");
    private final Label usersLabel = new Label("");

    private final ScreenStack screenStack;

    public GameScreen(ScreenStack screenStack) {
        super(SimGame.game.plat);
        this.screenStack = screenStack;
        new Pointer(game().plat, layer, true);
        game().plat.input().mouseEvents.connect(new Mouse.Dispatcher(layer, true));
        gameWorld.connect(update, paint);
        game().plat.input().keyboardEvents.connect(new DebugMode(gameWorld));
    }

    @Override
    public void wasShown() {
        super.wasShown();
        createUI();
        Jukebox.instance().loop(MusicCache.instance().GAME_THEME);
    }

    private void createUI() {
        GameBounds contentBounds = SimGame.game.bounds;
        Root root = iface.createRoot(new AbsoluteLayout(), makeStyleSheet(), layer);
        Group content = createContentGroup(root);
        root.add(AbsoluteLayout.at(content,
                contentBounds.x(), contentBounds.y(), contentBounds.width(), contentBounds.height()))
                .addStyles(Style.BACKGROUND.is(Background.solid(Palette.UNUSED_SPACE)))
                .setSize(size());
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

    @Override
    public Game game() {
        return SimGame.game;
    }

    private final class TopStatusBar extends Group {
        public TopStatusBar() {
            super(AxisLayout.horizontal().stretchByDefault());
            add(timeLabel);
            add(usersLabel);
            addStyles(Style.BACKGROUND.is(Background.solid(Palette.BACKGROUND)));
        }
    }

}
