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
import edu.bsu.cybersec.core.*;
import playn.core.*;
import playn.scene.Mouse;
import playn.scene.Pointer;
import pythagoras.f.Rectangle;
import react.Connection;
import react.Slot;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class GameScreen extends ScreenStack.UIScreen {
    private static final float IPHONE5_VERTICAL_ASPECT_RATIO = 9f / 16f;

    private final GameWorld.Systematized gameWorld;

    {
        ImageCache imageCache = ImageCache.instance();
        PlayableWorldFactory playableWorldFactory = new PlayableWorldFactory(imageCache);
        gameWorld = playableWorldFactory.createPlayableGameWorld();
    }

    @SuppressWarnings("unused")
    private tripleplay.entity.System timeRenderingSystem = new System(gameWorld, SystemPriority.UI_LEVEL.value) {
        {
            checkState(game().plat instanceof SimGamePlatform,
                    "The platform must provide the methods specified in SimGamePlatform");
            Entity updater = gameWorld.create(true).add(gameWorld.onUpdate);
            gameWorld.onUpdate.set(updater.id, new Updatable() {
                @Override
                public void update(Clock clock) {
                    final long tick = gameWorld.gameTime.get().now;
                    now = startTime + tick;
                    final String formatted = formatter.format(now);
                    timeLabel.text.update(formatted);
                }
            });
        }

        private final PlatformSpecificDateFormatter formatter =
                ((SimGamePlatform) game().plat).dateFormatter();

        private final long startTime = new java.util.Date().getTime();
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

    @SuppressWarnings("unused")
    tripleplay.entity.System progressRenderingSystem = new System(gameWorld, SystemPriority.UI_LEVEL.value) {
        @Override
        protected boolean isInterested(Entity entity) {
            return entity.has(gameWorld.developmentProgress);
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            super.update(clock, entities);
            checkState(entities.size() <= 1,
                    "I expected at most one featureId in development but found " + entities.size());
            for (int i = 0, limit = entities.size(); i < limit; i++) {
                int id = entities.get(i);
                progressLabel.text.update("Progress: " + gameWorld.developmentProgress.get(id) + " / " + gameWorld.goal.get(id));
            }
        }
    };

    @SuppressWarnings("unused")
    System vulnerabilityRenderingSystem = new System(gameWorld, SystemPriority.UI_LEVEL.value) {

        @Override
        protected boolean isInterested(Entity entity) {
            return true;
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            super.update(clock, entities);
            attackSurfaceLabel.text.update("Vulnerability estimate: " + gameWorld.vulnerability);
        }
    };

    private final SystemToggle systemToggle = new SystemToggle(
            gameWorld.updatingSystem,
            gameWorld.gameTimeSystem,
            gameWorld.userGenerationSystem,
            gameWorld.featureDevelopmentSystem,
            gameWorld.maintenanceSystem,
            gameWorld.expirySystem,
            gameWorld.eventTriggerSystem,
            gameWorld.featureGenerationSystem,
            gameWorld.learningSystem,
            gameWorld.exploitSystem);
    private final List<Element<?>> interactiveElements = Lists.newArrayList();
    private final Label timeLabel = new Label("");
    private final Label usersLabel = new Label("");
    private final Label progressLabel = new Label("");
    private final Label attackSurfaceLabel = new Label("");
    private final ToggleButton pauseButton = new ToggleButton("Pause");

    private interface State {
        void onEnter();

        void onExit();
    }

    private abstract class AbstractState implements State {
        protected void disableInteractiveElements() {
            for (Element<?> element : interactiveElements) {
                element.setEnabled(false);
            }
        }

        protected void enableInteractiveElements() {
            for (Element<?> element : interactiveElements) {
                element.setEnabled(true);
            }
        }
    }


    private final State playingState = new AbstractState() {

        private Connection connection;

        @Override
        public void onEnter() {
            checkState(connection == null, "There is a leaked connection");
            enableInteractiveElements();
        }

        @Override
        public void onExit() {
            connection.close();
            connection = null;
        }
    };

    private final State pausedState = new AbstractState() {

        @Override
        public void onEnter() {
            disableInteractiveElements();
            pauseButton.setEnabled(true);
            systemToggle.disable();
        }

        @Override
        public void onExit() {
            systemToggle.enable();
        }
    };

    private State state;

    public GameScreen() {
        new Pointer(game().plat, layer, true);
        game().plat.input().mouseEvents.connect(new Mouse.Dispatcher(layer, true));
        gameWorld.connect(update, paint);
        configurePauseButton();
        enterState(playingState);
        registerDebugHooks();
    }

    private void configurePauseButton() {
        pauseButton.selected().update(false);
        pauseButton.selected().connect(new Slot<Boolean>() {
            @Override
            public void onEmit(Boolean selected) {
                if (selected) {
                    enterState(pausedState);
                } else {
                    enterState(playingState);
                }
            }
        });
        interactiveElements.add(pauseButton);
    }

    private void enterState(State state) {
        checkNotNull(state);
        if (this.state != null) {
            this.state.onExit();
        }
        this.state = state;
        this.state.onEnter();
    }

    private void registerDebugHooks() {
        registerWorldLogSystemHook();
        registerArtificialEventHook();
    }

    private void registerWorldLogSystemHook() {
        final WorldLogSystem worldLogSystem = new WorldLogSystem(gameWorld);
        worldLogSystem.setEnabled(false);
        game().plat.input().keyboardEvents.connect(new Slot<Keyboard.Event>() {
            @Override
            public void onEmit(Keyboard.Event event) {
                if (isDebugTrigger(event)) {
                    worldLogSystem.setEnabled(true);
                }
            }

            private boolean isDebugTrigger(Event event) {
                if (event instanceof Keyboard.KeyEvent) {
                    Keyboard.KeyEvent keyEvent = (Keyboard.KeyEvent) event;
                    return keyEvent.down && keyEvent.key == Key.L;
                } else return false;
            }
        });
    }

    private void registerArtificialEventHook() {
        game().plat.input().keyboardEvents.connect(new Slot<Keyboard.Event>() {
            @Override
            public void onEmit(Keyboard.Event event) {
                if (isDebugTrigger(event)) {
                    makeArtificialEvent();
                }
            }

            private boolean isDebugTrigger(Event event) {
                if (event instanceof Keyboard.KeyEvent) {
                    Keyboard.KeyEvent keyEvent = (Keyboard.KeyEvent) event;
                    return keyEvent.down && keyEvent.key == Key.E;
                } else return false;
            }

            private void makeArtificialEvent() {
                final int hours = 4;
                Entity e = gameWorld.create(true).add(gameWorld.timeTrigger, gameWorld.event);
                gameWorld.timeTrigger.set(e.id, gameWorld.gameTime.get().now + 1);
                gameWorld.event.set(e.id, new NarrativeEvent(gameWorld,
                        "Your first worker wants to take a " + hours + "-hour nap. Is that allowed?",
                        new NarrativeEvent.Option("Yes", new Runnable() {
                            @Override
                            public void run() {
                                final Entity worker = gameWorld.workers.get(0);
                                final int wakeupTime = gameWorld.gameTime.get().now + ClockUtils.MS_PER_HOUR * hours;
                                gameWorld.tasked.set(worker.id,
                                        Task.createTask("Napping").expiringAt(wakeupTime).inWorld(gameWorld).build());
                                final Entity wakingUp = gameWorld.create(true)
                                        .add(gameWorld.timeTrigger, gameWorld.event);
                                gameWorld.timeTrigger.set(wakingUp.id, wakeupTime);
                                gameWorld.event.set(wakingUp.id, new Runnable() {
                                    @Override
                                    public void run() {
                                        gameWorld.tasked.set(worker.id, Task.MAINTENANCE);
                                        wakingUp.close();
                                    }
                                });
                            }
                        }),
                        new NarrativeEvent.Option("No", new Runnable() {
                            @Override
                            public void run() {
                                // Do nothing.
                            }
                        })));
            }
        });
    }

    @Override
    protected Root createRoot() {
        Rectangle contentBounds = new AspectRatioTool(IPHONE5_VERTICAL_ASPECT_RATIO).createBoundingBoxWithin(size());
        debug("contentBounds " + contentBounds);
        debug("size " + size());
        Root root = new Root(iface, new AbsoluteLayout(), makeStyleSheet());
        Group content = createContentGroup(root);
        return root
                .add(AbsoluteLayout.at(content,
                        contentBounds.x, contentBounds.y, contentBounds.width(), contentBounds.height()))
                .addStyles(Style.BACKGROUND.is(Background.solid(Palette.UNUSED_SPACE)))
                .setSize(size());
    }

    private void debug(String mesg) {
        game().plat.log().debug(mesg);
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
