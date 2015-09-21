package edu.bsu.cybersec.core.ui;

import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.*;
import playn.core.Clock;
import playn.core.Game;
import playn.core.Keyboard;
import playn.scene.Mouse;
import playn.scene.Pointer;
import pythagoras.f.Dimension;
import pythagoras.f.Rectangle;
import react.Connection;
import react.Slot;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public class GameScreen extends ScreenStack.UIScreen {
    private static final float IPHONE5_VERTICAL_ASPECT_RATIO = 9f / 16f;

    private final GameWorld.Systematized gameWorld;
    private final Entity company;

    {
        PlayableWorldFactory playableWorldFactory = new PlayableWorldFactory();
        gameWorld = playableWorldFactory.createPlayableGameWorld();
        company = playableWorldFactory.company;
    }

    @SuppressWarnings("unused")
    private tripleplay.entity.System timeRenderingSystem = new System(gameWorld, 0) {
        {
            checkState(game().plat instanceof SimGamePlatform,
                    "The platform must provide the methods specified in SimGamePlatform");
        }

        private final PlatformSpecificDateFormatter formatter =
                ((SimGamePlatform) game().plat).dateFormatter();

        private final long startTime = new java.util.Date().getTime();
        private long now = startTime;

        @Override
        protected boolean isInterested(Entity entity) {
            return entity.has(gameWorld.gameTime);
        }

        @Override
        protected void update(Clock clock, System.Entities entities) {
            checkState(entities.size() == 1, "I expected exactly one clock.");
            for (int i = 0, limit = entities.size(); i < limit; i++) {
                final int id = entities.get(i);
                final int tick = gameWorld.gameTime.get(id);
                now = startTime + tick;
            }
            final String formatted = formatter.format(now);
            timeLabel.text.update(formatted);
        }
    };

    @SuppressWarnings("unused")
    tripleplay.entity.System hudRenderingSystem = new System(gameWorld, 0) {
        @Override
        protected boolean isInterested(Entity entity) {
            return entity.id == company.id;
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            super.update(clock, entities);
            checkArgument(entities.size() == 1);
            float numberOfUsers = gameWorld.users.get(entities.get(0));
            usersLabel.text.update("Users: " + (int) numberOfUsers);
        }
    };

    @SuppressWarnings("unused")
    tripleplay.entity.System progressRenderingSystem = new System(gameWorld, 0) {
        @Override
        protected boolean isInterested(Entity entity) {
            return entity.has(gameWorld.featureId) && entity.has(gameWorld.progress);
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            super.update(clock, entities);
            checkState(entities.size() <= 1,
                    "I expected at most one featureId in development but found " + entities.size());
            for (int i = 0, limit = entities.size(); i < limit; i++) {
                int id = entities.get(i);
                progressLabel.text.update("Progress: " + gameWorld.progress.get(id) + " / " + gameWorld.goal.get(id));
            }
        }
    };

    @SuppressWarnings("unused")
    System attackSurfaceRenderingSystem = new System(gameWorld, 0) {

        @Override
        protected boolean isInterested(Entity entity) {
            return entity.has(gameWorld.attackSurface);
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            super.update(clock, entities);
            checkState(entities.size() == 1, "I expected only one entity to have an attack surface.");
            checkState(entities.get(0) == company.id, "I expect this to be the company");
            final int id = entities.get(0);
            float surface = gameWorld.attackSurface.get(id);
            attackSurfaceLabel.text.update("Attack surface: " + surface);
        }
    };

    private final SystemToggle systemToggle = new SystemToggle(
            gameWorld.gameTimeSystem,
            gameWorld.userGenerationSystem,
            gameWorld.featureDevelopmentSystem,
            gameWorld.maintenanceSystem,
            gameWorld.expirySystem);
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
        private Slot<Keyboard.Event> popupTriggerListener = new Slot<Keyboard.Event>() {
            @Override
            public void onEmit(Keyboard.Event event) {
                if (isPopupTrigger(event)) {
                    enterState(new PopupState("Hello, world!"));
                }
            }
        };

        private boolean isPopupTrigger(Keyboard.Event event) {
            return event instanceof Keyboard.KeyEvent
                    && ((Keyboard.KeyEvent) event).down;
        }

        @Override
        public void onEnter() {
            checkState(connection == null, "There is a leaked connection");
            enableInteractiveElements();
            connection = game().plat.input().keyboardEvents.connect(popupTriggerListener);
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

    private final class PopupState extends AbstractState {
        private final Group group;

        PopupState(String message) {
            group = new SizableGroup(AxisLayout.vertical(), new Dimension(300, 200));
            group.setStylesheet(makePopupStylesheet())
                    .add(new Label(message),
                            new Button("Dismiss")
                                    .onClick(new Slot<Button>() {
                                        @Override
                                        public void onEmit(Button event) {
                                            enterState(playingState);
                                        }
                                    }));
        }

        private Stylesheet makePopupStylesheet() {
            Stylesheet.Builder builder = SimpleStyles.newSheetBuilder(game().plat.graphics());
            builder.add(Group.class, Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
            builder.add(Label.class, Style.COLOR.is(Colors.CYAN));
            return builder.create();
        }

        @Override
        public void onEnter() {
            disableInteractiveElements();
            systemToggle.disable();
            _root.add(AbsoluteLayout.at(group, 200, 200));
        }

        @Override
        public void onExit() {
            enableInteractiveElements();
            systemToggle.enable();
            _root.remove(group);
        }
    }

    private State state;

    public GameScreen() {
        new Pointer(game().plat, layer, true);
        game().plat.input().mouseEvents.connect(new Mouse.Dispatcher(layer, false));
        gameWorld.connect(update, paint);
        configurePauseButton();
        enterState(playingState);
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

    @Override
    protected Root createRoot() {
        Rectangle contentBounds = new AspectRatioTool(IPHONE5_VERTICAL_ASPECT_RATIO).createBoundingBoxWithin(size());
        Group content = createContentGroup();
        return new Root(iface, new AbsoluteLayout(), makeStyleSheet())
                .add(AbsoluteLayout.at(content,
                        contentBounds.x, contentBounds.y, contentBounds.width(), contentBounds.height()))
                .setSize(size());
    }

    private Group createContentGroup() {
        final Group content = new Group(AxisLayout.vertical().gap(0).offStretch());
        content.add(new TopStatusBar()
                .setConstraint(Constraints.fixedHeight(30)));
        content.add(new MainUIGroup(gameWorld, iface)
                .setConstraint(AxisLayout.stretched()));
        content.add(new Group(AxisLayout.horizontal())
                .add(new Button("Buttons"), new Button("Go"), new Button("Here"))
                .setConstraint(Constraints.fixedHeight(30)))
                .addStyles(Style.BACKGROUND.is(Background.solid(Colors.BLACK)));
        return content;
    }

    private Stylesheet makeStyleSheet() {
        Stylesheet.Builder builder = SimpleStyles.newSheetBuilder(game().plat.graphics());
        builder.add(Label.class, Style.COLOR.is(Colors.WHITE));
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
            setConstraint(Constraints.fixedHeight(25));
            addStyles(Style.BACKGROUND.is(Background.solid(Colors.DARK_GRAY)));
        }
    }

}
