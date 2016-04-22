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
import com.google.common.collect.Maps;
import edu.bsu.cybersec.core.*;
import edu.bsu.cybersec.core.systems.GameTimeSystem;
import playn.core.Clock;
import playn.core.Font;
import playn.core.Tile;
import react.Slot;
import react.Value;
import react.ValueView;
import tripleplay.anim.Animation;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.entity.World;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.util.BoxPoint;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MainUIGroup extends Group {
    private static final float TRANSPARENT_AREA_WEIGHT = 1.0f;
    private static final float CONTROLS_AREA_WEIGHT = 1.0f;
    private static final List<Tile> BACKGROUNDS = ImmutableList.of(
            SimGame.game.assets.getTile(GameAssets.TileKey.BACKGROUND_1),
            SimGame.game.assets.getTile(GameAssets.TileKey.BACKGROUND_2),
            SimGame.game.assets.getTile(GameAssets.TileKey.BACKGROUND_4));
    private static final float WORKER_TOP_PERCENT_OF_HEIGHT = 0.15f;
    private static final float WORKER_X = SimGame.game.bounds.width() * 0.30f;

    private final Interface iface;
    private final GameWorld gameWorld;
    private final Value<Group> focus = Value.create(null);
    private final GameInteractionArea gameInteractionArea;
    private final Map<Integer, EmployeeView> developerViews = Maps.newTreeMap();
    private EmployeeViewUpdateSystem employeeViewUpdateSystem;
    private TaskIconFactory taskIconFactory;

    public MainUIGroup(final GameWorld gameWorld, final Interface iface, Root root) {
        super(AxisLayout.vertical().offStretch().gap(0));
        this.iface = checkNotNull(iface);
        this.gameWorld = checkNotNull(gameWorld);
        this.taskIconFactory = new TaskIconFactory();
        employeeViewUpdateSystem = new EmployeeViewUpdateSystem(gameWorld);
        gameInteractionArea = new GameInteractionArea(gameWorld, iface);
        configureUI(root);
        animateFocusChanges();
    }

    private void configureUI(Root root) {
        Iterator<Tile> backgroundIterator = BACKGROUNDS.iterator();
        for (Entity e : gameWorld.workers) {
            final int id = e.id;
            EmployeeView employeeView = new EmployeeView(id, root, backgroundIterator.next());
            developerViews.put(id, employeeView);
            add(employeeView);
        }
        gameInteractionArea.setConstraint(AxisLayout.stretched(2));
        add(gameInteractionArea);
        new EmployeeAtWorkSystem(gameWorld);
    }

    private void animateFocusChanges() {
        focus.connect(new ValueView.Listener<Group>() {
            private static final float SELECTED_GROUP_WEIGHT = 3;
            private static final float UNSELECTED_GROUP_WEIGHT = 1;
            private static final float DEFAULT_CONTENT_GROUP_WEIGHT = 2;
            private static final float INVISIBLE_CONTENT_GROUP_WEIGHT = 0;
            private static final float ANIMATION_DURATION = 1000f;

            @Override
            public void onChange(Group newSelection, Group previousSelection) {
                if (previousSelection != null) {
                    shrinkWorkerGroup(previousSelection);
                }
                if (newSelection != null) {
                    expandWorkerGroup(newSelection);
                }
                if (previousSelection != null && newSelection == null) {
                    expandContentArea();
                }
                if (previousSelection == null && newSelection != null) {
                    shrinkContentArea();
                }
            }

            private void shrinkWorkerGroup(Group previousSelection) {
                iface.anim.tween(new AxisLayoutWeightAnimationValue(previousSelection))
                        .from(SELECTED_GROUP_WEIGHT).to(UNSELECTED_GROUP_WEIGHT).in(ANIMATION_DURATION).easeOut();
            }

            private void expandWorkerGroup(Group newSelection) {
                iface.anim.tween(new AxisLayoutWeightAnimationValue(newSelection))
                        .from(UNSELECTED_GROUP_WEIGHT).to(SELECTED_GROUP_WEIGHT).in(ANIMATION_DURATION).easeOut();
            }

            private void expandContentArea() {
                iface.anim.tween(new AxisLayoutWeightAnimationValue(gameInteractionArea))
                        .from(INVISIBLE_CONTENT_GROUP_WEIGHT).to(DEFAULT_CONTENT_GROUP_WEIGHT).in(ANIMATION_DURATION).easeOut();
            }

            private void shrinkContentArea() {
                iface.anim.tween(new AxisLayoutWeightAnimationValue(gameInteractionArea))
                        .from(DEFAULT_CONTENT_GROUP_WEIGHT).to(INVISIBLE_CONTENT_GROUP_WEIGHT).in(ANIMATION_DURATION).easeOut();
            }
        });
    }

    private final class TaskItem extends MenuItem {

        public final int taskId;

        public TaskItem(int taskId) {
            super(gameWorld.name.get(taskId));
            this.taskId = taskId;
            icon.update(taskIconFactory.getIcon(taskId));
        }
    }

    private final class EmployeeView extends Group {

        private final int id;
        private final Value<Integer> developmentSkill = Value.create(0);
        private final Value<Integer> maintenanceSkill = Value.create(0);
        private final Value<Boolean> atWork = Value.create(true);

        EmployeeView(final int id, Root root, Tile background) {
            super(AxisLayout.horizontal().offStretch());
            this.id = id;
            checkNotNull(root);

            addStyles(Style.BACKGROUND.is(
                    ExpandableBackground.background(background)
                            .withWorkHours(((GameWorld.Systematized) gameWorld).workHoursSystem)))
                    .setConstraint(AxisLayout.stretched());
            add(createTransparentClickableArea(),
                    createControlsAndBioGroup(root));
            layer.add(gameWorld.sprite.get(id));
            gameWorld.position.setX(id, WORKER_X);
            animateBasedOnAtWorkStatus();
        }

        private Element<?> createTransparentClickableArea() {
            return new TouchableLabel("")
                    .onClick(new Slot<TouchableLabel>() {
                        @Override
                        public void onEmit(TouchableLabel event) {
                            String name = gameWorld.profile.get(id).firstName;
                            if (focus.get() != EmployeeView.this) {
                                SimGame.game.event.emit(TrackedEvent.game().action("expand").label(name));
                                focus.update(EmployeeView.this);
                            } else {
                                SimGame.game.event.emit(TrackedEvent.game().action("collapse").label(name));
                                focus.update(null);
                            }
                        }
                    })
                    .setConstraint(AxisLayout.stretched(TRANSPARENT_AREA_WEIGHT));
        }

        private Group createControlsAndBioGroup(Root root) {
            final EmployeeProfile profile = gameWorld.profile.get(id);
            final float borderThickness = percentOfViewHeight(0.005f);
            Group employeeDataGroup = new Group(AxisLayout.vertical().offStretch());
            for (EmployeeProfile.Credential credential : profile.credentials) {
                employeeDataGroup.add(wrappingLabel(credential.name + " (" + credential.provider + ")"));
            }
            employeeDataGroup.add(wrappingLabel(profile.bio))
                    .addStyles(Style.BACKGROUND.is(
                            Background.bordered(Palette.DIALOG_BACKGROUND, Palette.DIALOG_BORDER, borderThickness)
                                    .inset(borderThickness * 2)));
            final float spaceAroundNameAndTaskArea = percentOfViewHeight(0.03f);
            final Font nameFont = FontCache.instance().REGULAR.derive(percentOfViewHeight(0.03f));
            return new Group(AxisLayout.vertical())
                    .add(new Shim(0, spaceAroundNameAndTaskArea),
                            dialogStyledLabel(profile.firstName + " " + profile.lastName)
                                    .addStyles(Style.FONT.is(nameFont),
                                            Style.COLOR.is(Palette.NAME_COLOR)),
                            createSkillSummaryGroup(),
                            new TaskSelector(root, gameWorld.entity(id)),
                            new Shim(0, spaceAroundNameAndTaskArea),
                            employeeDataGroup,
                            new Shim(0, 0).setConstraint(AxisLayout.stretched()))
                    .setConstraint(AxisLayout.stretched(CONTROLS_AREA_WEIGHT));
        }

        private Group createSkillSummaryGroup() {
            final int color = Palette.NAME_COLOR;
            return new Group(AxisLayout.horizontal())
                    .add(new Label(taskIconFactory.getIcon(gameWorld.developmentTaskId)).addStyles(Style.COLOR.is(color)),
                            createSkillLabel(developmentSkill),
                            new Shim(percentOfViewHeight(0.001f), 0),
                            new Label(taskIconFactory.getIcon(gameWorld.maintenanceTaskId)).addStyles(Style.COLOR.is(color)),
                            createSkillLabel(maintenanceSkill));
        }

        private Element<?> createSkillLabel(Value<Integer> skill) {
            final Label updatingLabel = new Label(skill.get().toString());
            updatingLabel.text.connect(new ValueView.Listener<String>() {
                @Override
                public void onChange(String value, String oldValue) {
                    iface.anim.tween(new LabelColorHighlightAmount(updatingLabel))
                            .from(1)
                            .to(0)
                            .in(850f)
                            .easeIn();
                }
            });
            skill.connect(new ValueView.Listener<Integer>() {
                @Override
                public void onChange(Integer value, Integer oldValue) {
                    updatingLabel.text.update(value.toString());
                }
            });
            return updatingLabel;
        }

        private Element<?> dialogStyledLabel(String s) {
            return new Label(s).addStyles(Style.COLOR.is(Palette.DIALOG_FOREGROUND));
        }

        private Element<?> wrappingLabel(String s) {
            return dialogStyledLabel(s)
                    .addStyles(Style.TEXT_WRAP.on,
                            Style.HALIGN.left);
        }

        private float percentOfViewHeight(float percent) {
            return SimGame.game.bounds.percentOfHeight(percent);
        }

        private void animateBasedOnAtWorkStatus() {
            atWork.connect(new Slot<Boolean>() {
                @Override
                public void onEmit(Boolean aBoolean) {
                    final int sign = aBoolean ? 1 : -1;
                    iface.anim.tween(new Animation.Value() {
                        @Override
                        public float initial() {
                            return gameWorld.position.getX(id);
                        }

                        @Override
                        public void set(float value) {
                            gameWorld.position.setX(id, value);
                        }
                    })
                            .to(sign * WORKER_X)
                            .in(500f)
                            .easeInOut();
                }
            });
        }

        void update() {
            final int displayedDevelopmentSkill = (int) gameWorld.developmentSkill.get(id);
            developmentSkill.update(displayedDevelopmentSkill);
            final int displayedMaintenanceSkill = (int) gameWorld.maintenanceSkill.get(id);
            maintenanceSkill.update(displayedMaintenanceSkill);
            updateYPos();
        }

        private void updateYPos() {
            gameWorld.position.setY(id, size().height() * WORKER_TOP_PERCENT_OF_HEIGHT);
        }
    }

    private final class TaskSelector extends Button {
        int selectedTaskId;

        TaskSelector(Root root, final Entity worker) {
            super();
            select(gameWorld.task.get(worker.id));
            final MenuHost menuHost = new MenuHost(iface, root);
            BoxPoint popUnder = new BoxPoint(0, 1, 0, 2);
            addStyles(MenuHost.TRIGGER_POINT.is(MenuHost.relative(popUnder)));
            onClick(new Slot<Button>() {
                private boolean timeEnabledStatus;
                private final GameTimeSystem gameTimeSystem = ((GameWorld.Systematized) gameWorld).gameTimeSystem;

                @Override
                public void onEmit(Button button) {
                    if (button.isEnabled()) {
                        stopGameTime();
                        MenuHost.Pop pop = new MenuHost.Pop(button, createMenu());
                        pop.menu.itemTriggered().connect(taskUpdater(button));
                        pop.menu.deactivated().connect(new Slot<Menu>() {
                            @Override
                            public void onEmit(Menu elements) {
                                restoreGameTimeSystemToPreviousState();
                            }
                        });
                        menuHost.popup(pop);
                    }
                }

                private void stopGameTime() {
                    timeEnabledStatus = gameTimeSystem.isEnabled();
                    gameTimeSystem.setEnabled(false);
                }


                private void restoreGameTimeSystemToPreviousState() {
                    gameTimeSystem.setEnabled(timeEnabledStatus);
                }

                private Slot<MenuItem> taskUpdater(final Button button) {
                    return new Slot<MenuItem>() {
                        @Override
                        public void onEmit(MenuItem menuItem) {
                            button.text.update(menuItem.text.get());
                            int assignedTaskId = ((TaskItem) menuItem).taskId;
                            gameWorld.task.set(worker.id, assignedTaskId);
                            logEvent(assignedTaskId);
                        }

                        private void logEvent(int assignedTaskId) {
                            String name = gameWorld.profile.get(worker.id).firstName;
                            String task = gameWorld.name.get(assignedTaskId);
                            SimGame.game.event.emit(TrackedEvent.game().action("task").label(name + ":" + task));
                        }
                    };
                }


                private Menu createMenu() {
                    Menu menu = new Menu(AxisLayout.vertical().offStretch().gap(3));
                    menu.add(new TaskItem(gameWorld.developmentTaskId));
                    menu.add(new TaskItem(gameWorld.maintenanceTaskId));
                    return menu;
                }
            });
            employeeViewUpdateSystem.track(worker.id, this);
            showTaskNameUpdates();
        }

        private void showTaskNameUpdates() {
            Entity taskNameUpdater = gameWorld.create(true).add(gameWorld.onUpdate);
            gameWorld.onUpdate.set(taskNameUpdater.id, new Updatable() {
                @Override
                public void update(Clock clock) {
                    setText(gameWorld.name.get(selectedTaskId));
                }
            });
        }

        public void select(int taskId) {
            if (selectedTaskId != taskId) {
                selectedTaskId = taskId;
                setEnabled(TaskFlags.REASSIGNABLE.isSet(gameWorld.taskFlags.get(taskId)));
                setText(gameWorld.name.get(taskId));
                Icon taskIcon = taskIconFactory.getIcon(selectedTaskId);
                icon.update(taskIcon);
            }
        }
    }

    private class EmployeeViewUpdateSystem extends tripleplay.entity.System {

        private final GameWorld world;

        private final Map<Integer, TaskSelector> map = Maps.newHashMap();

        public EmployeeViewUpdateSystem(GameWorld world) {
            super(world, SystemPriority.UI_LEVEL.value);
            this.world = checkNotNull(world);
        }

        public void track(int id, TaskSelector selector) {
            checkArgument(!map.containsKey(id));
            checkArgument(!map.containsValue(selector));
            map.put(id, selector);
        }

        @Override
        protected boolean isInterested(Entity entity) {
            return map.containsKey(entity.id);
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            super.update(clock, entities);
            for (int i = 0, limit = entities.size(); i < limit; i++) {
                final int id = entities.get(i);
                ensureSelectedTaskMatchesAssignedTask(id);
                updateDeveloperInfoArea(id);
            }
        }

        private void ensureSelectedTaskMatchesAssignedTask(int id) {
            TaskSelector sel = map.get(id);
            int taskId = world.task.get(id);
            if (sel.selectedTaskId != taskId) {
                sel.select(taskId);
            }
        }

        private void updateDeveloperInfoArea(int id) {
            EmployeeView view = checkNotNull(developerViews.get(id), "Missing developer");
            view.update();
        }
    }

    private final class EmployeeAtWorkSystem extends System {

        protected EmployeeAtWorkSystem(World world) {
            super(world, SystemPriority.UI_LEVEL.value);
        }

        @Override
        protected boolean isInterested(Entity entity) {
            return developerViews.containsKey(entity.id);
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            for (int i = 0, limit = entities.size(); i < limit; i++) {
                final int id = entities.get(i);
                developerViews.get(id).atWork.update(isAtWork(id));
            }
        }

        private boolean isAtWork(int id) {
            return ((GameWorld.Systematized) gameWorld).workHoursSystem.isWorkHours().get()
                    && TaskFlags.BOUND_TO_WORKDAY.isSet(gameWorld.taskFlags.get(gameWorld.task.get(id)));
        }
    }

    private final class TaskIconFactory {

        public Icon getIcon(int taskId) {
            final int taskFlags = gameWorld.taskFlags.get(taskId);
            if (TaskFlags.any(TaskFlags.DEVELOPMENT, TaskFlags.MAINTENANCE).in(taskFlags)) {
                final Tile tile = TaskFlags.DEVELOPMENT.isSet(taskFlags)
                        ? SimGame.game.assets.getImage(GameAssets.ImageKey.DEVELOPMENT).tile()
                        : SimGame.game.assets.getImage(GameAssets.ImageKey.MAINTENANCE).tile();
                final float imageHeight = tile.height();
                final float viewHeight = SimGame.game.bounds.height();
                final float percentOfScreenHeight = 0.04f;
                final float scale = (viewHeight * percentOfScreenHeight) / imageHeight;
                return Icons.scaled(Icons.image(tile), scale);
            } else {
                return null;
            }
        }
    }
}

