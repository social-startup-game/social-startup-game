package edu.bsu.cybersec.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import playn.core.Clock;
import playn.core.Game;
import playn.core.Keyboard;
import pythagoras.f.Point;
import react.Slot;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.util.BoxPoint;
import tripleplay.util.Colors;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class GameScreen extends ScreenStack.UIScreen {
    private static final float SECONDS_PER_HOUR = 60 * 60;
    private static final String DOWN_ARROW = "\u25BC";
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

        @SuppressWarnings("unused")
        System workerRenderingSystem = new System(this, 0) {

            private final Point p = new Point();
            private final Map<Integer, WorkerUI> groups = Maps.newHashMap();

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(name)
                        && entity.has(developmentSkill)
                        && entity.has(maintenanceSkill)
                        && entity.has(position);
            }

            @Override
            protected void wasAdded(Entity entity) {
                super.wasAdded(entity);
                checkState(!groups.containsKey(entity.id), "Group for entity already exists");
                position.get(entity.id, p);
                String workerName = name.get(entity.id);
                WorkerUI workerUI = new WorkerUI(entity.id);
                groups.put(entity.id, workerUI);
                _root.add(AbsoluteLayout.at(workerUI, p));
            }

            @Override
            protected void wasRemoved(Entity entity, int index) {
                super.wasRemoved(entity, index);
                groups.remove(entity.id);
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    updateWorkerUI(entities.get(i));
                }
            }

            private void updateWorkerUI(final int id) {
                WorkerUI ui = groups.get(id);
                ui.nameLabel.text.update(name.get(id));
                ui.developmentSkillLabel.text.update(String.valueOf(developmentSkill.get(id)));
                ui.maintenanceSkillLabel.text.update(String.valueOf(maintenanceSkill.get(id)));
            }

            final class WorkerUI extends Group {
                final Label nameLabel = new Label();
                final Label developmentSkillLabel = new Label();
                final Label maintenanceSkillLabel = new Label();

                private WorkerUI(final int id) {
                    super(AxisLayout.vertical());
                    add(nameLabel,
                            new Group(AxisLayout.horizontal())
                                    .add(new Label("D: "),
                                            developmentSkillLabel),
                            new Group(AxisLayout.horizontal())
                                    .add(new Label("M: "),
                                            maintenanceSkillLabel),
                            new TaskSelector(_root, entity(id)));
                }
            }
        };
    };

    private final List<Element<?>> interactiveElements = Lists.newArrayList();
    private final Label timeLabel = new Label("");
    private final Label usersLabel = new Label("");
    private final Label progressLabel = new Label("");
    private final Label attackSurfaceLabel = new Label("");
    private final Button attackButton = new AttackButton();
    private final ToggleButton pauseButton = new ToggleButton("Pause");

    public GameScreen() {
        world.connect(update, paint);
        initializeWorld();
        configurePauseButton();
        game().plat.input().keyboardEvents.connect(new Slot<Keyboard.Event>() {
            @Override
            public void onEmit(Keyboard.Event event) {
                if (isPopupTrigger(event)) {
                    game().plat.log().debug("Bringing up pop-up dialog box " + event);
                    for (Element<?> element : interactiveElements) {
                        element.setEnabled(false);
                    }
                    pauseButton.selected().update(true);

                    final Group group = new Group(AxisLayout.vertical());
                    group.setStylesheet(makePopupStylesheet())
                            .setConstraint(Constraints.fixedSize(600, 400))
                            .add(new Label("THIS IS A POPUP"),
                                    new Button("Dismiss")
                                            .onClick(new Slot<Button>() {
                                                @Override
                                                public void onEmit(Button event) {
                                                    for (Element<?> element : interactiveElements) {
                                                        element.setEnabled(true);
                                                    }
                                                    pauseButton.selected().update(false);
                                                    _root.remove(group);
                                                }
                                            }));
                    _root.add(AbsoluteLayout.at(group, 200, 200));
                }
            }

            private boolean isPopupTrigger(Keyboard.Event event) {
                return event instanceof Keyboard.KeyEvent
                        && ((Keyboard.KeyEvent) event).down;
            }

            private Stylesheet makePopupStylesheet() {
                Stylesheet.Builder builder = SimpleStyles.newSheetBuilder(game().plat.graphics());
                builder.add(Group.class, Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
                builder.add(Label.class, Style.COLOR.is(Colors.CYAN));
                return builder.create();
            }
        });
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
        final SystemToggle toggle = new SystemToggle(
                world.gameTimeSystem,
                world.userGenerationSystem,
                world.featureDevelopmentSystem,
                world.maintenanceSystem,
                world.expirySystem);
        pauseButton.selected().connect(new Slot<Boolean>() {
            @Override
            public void onEmit(Boolean selected) {
                if (selected) {
                    toggle.disable();
                } else {
                    toggle.enable();
                }
            }
        });
        interactiveElements.add(pauseButton);
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, new AbsoluteLayout(), makeStyleSheet());
        root.add(AbsoluteLayout.at(timeLabel, 50, 50));
        root.add(AbsoluteLayout.at(usersLabel, 50, 100));
        root.add(AbsoluteLayout.at(progressLabel, 50, 150));
        root.add(AbsoluteLayout.at(attackSurfaceLabel, 50, 200));
        root.add(AbsoluteLayout.at(pauseButton, 400, 50));
        root.add(AbsoluteLayout.at(attackButton, 400, 100));
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

    final class TaskSelector extends Button {
        private final TaskFormatter formatter = new TaskFormatter();

        TaskSelector(Root root, final Entity worker) {
            super();
            setTextBasedOnCurrentTaskOf(worker);
            final MenuHost menuHost = new MenuHost(iface, root);
            BoxPoint popUnder = new BoxPoint(0, 1, 0, 2);
            addStyles(MenuHost.TRIGGER_POINT.is(MenuHost.relative(popUnder)));
            onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button button) {
                    MenuHost.Pop pop = new MenuHost.Pop(button,
                            createMenu());
                    pop.menu.itemTriggered().connect(updater(button));
                    menuHost.popup(pop);
                }

                private Slot<MenuItem> updater(final Button button) {
                    return new Slot<MenuItem>() {
                        @Override
                        public void onEmit(MenuItem menuItem) {
                            button.text.update(menuItem.text.get() + " " + DOWN_ARROW);
                            int assignedTask = formatter.asTask(menuItem.text.get());
                            world.tasked.set(worker.id, assignedTask);
                            worker.didChange();
                        }
                    };
                }

                private Menu createMenu() {
                    Menu menu = new Menu(AxisLayout.vertical().offStretch().gap(3));
                    for (int task : Task.VALUES) {
                        menu.add(new MenuItem(formatter.format(task)));
                    }
                    return menu;
                }
            });
            interactiveElements.add(this);
        }

        private void setTextBasedOnCurrentTaskOf(Entity worker) {
            final int currentTask = world.tasked.get(worker.id);
            String taskName = formatter.format(currentTask);
            text.update(taskName + " " + DOWN_ARROW);
        }

    }

    private final class AttackButton extends Button {
        AttackButton() {
            super("Force an attack");
            onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button button) {
                    Entity attack = world.create(true)
                            .add(world.usersPerSecond, world.expiresIn, world.companyId);
                    world.usersPerSecond.set(attack.id, -2);
                    world.expiresIn.set(attack.id, 2000);
                    world.companyId.set(attack.id, company.id);
                }
            });
            interactiveElements.add(this);
        }
    }

}
