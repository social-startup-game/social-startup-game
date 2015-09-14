package edu.bsu.cybersec.core.ui;

import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.*;
import playn.core.Clock;
import playn.core.Game;
import playn.core.Keyboard;
import playn.scene.Mouse;
import playn.scene.Pointer;
import pythagoras.f.Dimension;
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
    private static final float SECONDS_PER_HOUR = 60 * 60;

    private Entity company;
    private Entity[] developers;
    private GameWorld.Systematized world = new GameWorld.Systematized() {
        @SuppressWarnings("unused")
        private tripleplay.entity.System timeRenderingSystem = new System(this, 0) {
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
                return entity.has(gameTime);
            }

            @Override
            protected void update(Clock clock, System.Entities entities) {
                checkState(entities.size() == 1, "I expected exactly one clock.");
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int id = entities.get(i);
                    final int tick = gameTime.get(id);
                    now = startTime + tick;
                }
                final String formatted = formatter.format(now);
                timeLabel.text.update(formatted);
            }
        };

        @SuppressWarnings("unused")
        tripleplay.entity.System hudRenderingSystem = new System(this, 0) {
            @Override
            protected boolean isInterested(Entity entity) {
                return entity.id == company.id;
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                checkArgument(entities.size() == 1);
                float numberOfUsers = users.get(entities.get(0));
                usersLabel.text.update("Users: " + (int) numberOfUsers);
            }
        };

        @SuppressWarnings("unused")
        tripleplay.entity.System progressRenderingSystem = new System(this, 0) {
            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(featureId) && entity.has(progress);
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                checkState(entities.size() <= 1,
                        "I expected at most one featureId in development but found " + entities.size());
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    int id = entities.get(i);
                    progressLabel.text.update("Progress: " + progress.get(id) + " / " + goal.get(id));
                }
            }
        };

        @SuppressWarnings("unused")
        System attackSurfaceRenderingSystem = new System(this, 0) {

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(attackSurface);
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                checkState(entities.size() == 1, "I expected only one entity to have an attack surface.");
                checkState(entities.get(0) == company.id, "I expect this to be the company");
                final int id = entities.get(0);
                float surface = attackSurface.get(id);
                attackSurfaceLabel.text.update("Attack surface: " + surface);
            }
        };
    };

    private final SystemToggle systemToggle = new SystemToggle(
            world.gameTimeSystem,
            world.userGenerationSystem,
            world.featureDevelopmentSystem,
            world.maintenanceSystem,
            world.expirySystem);
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
        world.connect(update, paint);
        initializeWorld();
        configurePauseButton();
        enterState(playingState);
    }

    private void initializeWorld() {
        makeCompany();
        makeClock();
        makeExistingFeature();
        makeFeatureInDevelopment();
        makeDevelopers(3);
    }

    private void makeCompany() {
        company = world.create(true)
                .add(world.type,
                        world.users,
                        world.attackSurface);
        world.type.set(company.id, Type.COMPANY);
        world.users.set(company.id, 0);
        world.attackSurface.set(company.id, 0);
    }

    private void makeClock() {
        Entity clock = world.create(true).add(world.type, world.gameTime, world.gameTimeScale);
        final int id = clock.id;
        world.type.set(id, Type.CLOCK);
        world.gameTime.set(id, 0);
        world.gameTimeScale.set(id, SECONDS_PER_HOUR);
    }

    private Entity[] makeDevelopers(int number) {
        checkArgument(number >= 0);
        checkState(developers == null, "Expected developers not yet to be initialized");
        developers = new Entity[number];
        for (int i = 0; i < number; i++) {
            developers[i] = makeDeveloper(i * 200 + 100);
        }
        return developers;
    }

    private Entity makeDeveloper(float x) {
        Entity developer = world.create(true)
                .add(world.developmentSkill,
                        world.tasked,
                        world.companyId,
                        world.maintenanceSkill,
                        world.name,
                        world.position);
        world.tasked.set(developer.id, Task.IDLE);
        world.developmentSkill.set(developer.id, 5);
        world.maintenanceSkill.set(developer.id, 0.02f);
        world.companyId.set(developer.id, company.id);
        world.name.set(developer.id, "Bob Ross");
        world.position.set(developer.id, x, 250);
        return developer;
    }

    private void makeExistingFeature() {
        Entity userGeneratingEntity = world.create(true).add(world.usersPerSecond, world.companyId, world.exposure);
        world.usersPerSecond.set(userGeneratingEntity.id, 1);
        world.companyId.set(userGeneratingEntity.id, company.id);
        world.attackSurface.set(company.id, 0.05f);
    }

    private void makeFeatureInDevelopment() {
        Entity feature = world.create(false)
                .add(world.usersPerSecond, world.companyId, world.exposure);
        world.usersPerSecond.set(feature.id, 25);
        world.companyId.set(feature.id, company.id);
        world.exposure.set(feature.id, 0.20f);

        Entity development = world.create(true)
                .add(world.progress, world.goal, world.featureId);
        world.progress.set(development.id, 0);
        world.goal.set(development.id, 20);
        world.featureId.set(development.id, feature.id);
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
        Root root = new Root(iface, AxisLayout.vertical().gap(0).offStretch(), makeStyleSheet());
        root.add(new TopStatusBar()
                .setConstraint(Constraints.fixedHeight(30)));
        root.add(new MainUIGroup(world, iface)
                .setConstraint(AxisLayout.stretched()));
        root.add(new Group(AxisLayout.horizontal())
                .add(new Button("Buttons"), new Button("Go"), new Button("Here"))
                .setConstraint(Constraints.fixedHeight(30)));
        root.setSize(size());
        return root;
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
