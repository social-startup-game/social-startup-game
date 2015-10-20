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

import com.google.common.collect.Maps;
import edu.bsu.cybersec.core.*;
import playn.core.Clock;
import playn.core.Image;
import react.Slot;
import react.Value;
import react.ValueView;
import tripleplay.entity.Entity;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.util.BoxPoint;
import tripleplay.util.Colors;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MainUIGroup extends Group {
    private static final String DOWN_ARROW = "\u25BC";
    private static final float TRANSPARENT_AREA_WEIGHT = 1.0f;
    private static final float CONTROLS_AREA_WEIGHT = 1.0f;

    private final Interface iface;
    private final GameWorld gameWorld;
    private final Value<Group> focus = Value.create(null);
    private Group contentGroup;
    private final GameInteractionArea gameInteractionArea;

    private final Image employeeBackground = ImageCache.instance().EMPLOYEE_BG;

    public MainUIGroup(final GameWorld gameWorld, final Interface iface) {
        super(AxisLayout.vertical().offStretch().gap(0));
        this.iface = checkNotNull(iface);
        this.gameWorld = checkNotNull(gameWorld);
        gameInteractionArea = new GameInteractionArea(gameWorld, iface);
        setupUIConfigurationSystem();
        animateFocusChanges();
    }

    private void setupUIConfigurationSystem() {
        new tripleplay.entity.System(gameWorld, SystemPriority.UI_LEVEL.value) {

            private boolean configured = false;
            private final Map<Integer, DeveloperView> developerViews = Maps.newTreeMap();

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(gameWorld.employeeNumber);
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                checkArgument(entities.size() == 3, "Current UI layout assumes three workers.");
                if (!configured) {
                    configureUI(entities);
                } else {
                    updateDeveloperInfoArea(entities);
                }
            }

            private void configureUI(Entities entities) {
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int id = entities.get(i);
                    DeveloperView developerView = new DeveloperView(id, root());
                    developerViews.put(id, developerView);
                    add(developerView);
                }
                contentGroup = new Group(AxisLayout.horizontal().offStretch().stretchByDefault())
                        .add(gameInteractionArea)
                        .setConstraint(AxisLayout.stretched(2));
                add(contentGroup);
                configured = true;
            }

            private void updateDeveloperInfoArea(Entities entities) {
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int id = entities.get(i);
                    updateDeveloperInfoArea(id);
                }
            }

            private void updateDeveloperInfoArea(int id) {
                DeveloperView view = checkNotNull(developerViews.get(id), "Missing developer");
                view.update();
            }
        };
    }

    private void animateFocusChanges() {
        focus.connect(new ValueView.Listener<Group>() {
            private static final float SELECTED_GROUP_WEIGHT = 3;
            private static final float UNSELECTED_GROUP_WEIGHT = 1;
            private static final float DEFAULT_CONTENT_GROUP_WEIGHT = 2;
            private static final float INVISIBLE_CONTENT_GROUP_WEIGHT = 0;
            private static final float ANIMATION_DURATION = 1000f;
            private static final float TRANSPARENT = 0f;
            private static final float OPAQUE = 1f;

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
                iface.anim.tween(new AxisLayoutWeightAnimationValue(contentGroup))
                        .from(INVISIBLE_CONTENT_GROUP_WEIGHT).to(DEFAULT_CONTENT_GROUP_WEIGHT).in(ANIMATION_DURATION).easeOut();
                iface.anim.tweenAlpha(contentGroup.layer)
                        .from(TRANSPARENT).to(OPAQUE).in(ANIMATION_DURATION / 2);
            }

            private void shrinkContentArea() {
                iface.anim.tween(new AxisLayoutWeightAnimationValue(contentGroup))
                        .from(DEFAULT_CONTENT_GROUP_WEIGHT).to(INVISIBLE_CONTENT_GROUP_WEIGHT).in(ANIMATION_DURATION).easeOut();
                iface.anim.tweenAlpha(contentGroup.layer)
                        .from(OPAQUE).to(TRANSPARENT).in(ANIMATION_DURATION / 2);
            }
        });
    }

    private final class DeveloperView extends Group {

        private final int id;
        private Label developmentSkillLabel;
        private Label maintenanceSkillLabel;

        DeveloperView(int id, Root root) {
            super(AxisLayout.horizontal().offStretch());
            this.id = id;
            checkNotNull(root);

            final Image image = gameWorld.image.get(id);
            addStyles(Style.BACKGROUND.is(
                    ExpandableParallaxBackground.foreground(image).background(employeeBackground.tile())))
                    .setConstraint(AxisLayout.stretched());
            add(createTransparentClickableArea(),
                    createControlsAndBioGroup(root));
        }

        private Element createTransparentClickableArea() {
            return new ClickableLabel("")
                    .onClick(new Slot<ClickableLabel>() {
                        @Override
                        public void onEmit(ClickableLabel event) {
                            if (focus.get() != DeveloperView.this) {
                                focus.update(DeveloperView.this);
                            } else {
                                focus.update(null);
                            }
                        }
                    })
                    .setConstraint(AxisLayout.stretched(TRANSPARENT_AREA_WEIGHT));
        }

        private Group createControlsAndBioGroup(Root root) {
            final Name name = gameWorld.name.get(id);
            final float borderThickness = percentOfViewHeight(0.005f);
            Group employeeDataGroup = new Group(AxisLayout.vertical())
                    .add(new Label(name.fullName),
                            createDevelopmentSkillBlock(),
                            createMaintenanceSkillBlock(),
                            wrappingLabel("Degree: Bachelor of Science"),
                            wrappingLabel("Discipline: Computer Science"),
                            wrappingLabel("University: Ball State"))
                    .addStyles(Style.BACKGROUND.is(
                            Background.bordered(Colors.BLACK, Colors.WHITE, borderThickness)
                                    .inset(borderThickness)));
            final float spaceAroundNameAndTaskArea = percentOfViewHeight(0.08f);
            return new Group(AxisLayout.vertical())
                    .add(new Shim(0, spaceAroundNameAndTaskArea),
                            new Label(name.shortName),
                            new TaskSelector(root, gameWorld.entity(id)),
                            new Shim(0, spaceAroundNameAndTaskArea),
                            new Shim(0, 0).setConstraint(AxisLayout.stretched()),
                            employeeDataGroup,
                            new Shim(0, 0).setConstraint(AxisLayout.stretched()))
                    .setConstraint(AxisLayout.stretched(CONTROLS_AREA_WEIGHT));
        }

        private Element<?> wrappingLabel(String s) {
            return new Label(s)
                    .addStyles(Style.TEXT_WRAP.on);
        }

        private Element<?> createDevelopmentSkillBlock() {
            return new Group(AxisLayout.horizontal())
                    .add(new Label("Development: "),
                            developmentSkillLabel = new Label("0"));
        }

        private Element<?> createMaintenanceSkillBlock() {
            return new Group(AxisLayout.horizontal())
                    .add(new Label("Maintenance: "),
                            maintenanceSkillLabel = new Label("0"));
        }

        private float percentOfViewHeight(float v) {
            return SimGame.game.plat.graphics().viewSize.height() * v;
        }

        void update() {
            final int displayedDevelopmentSkill = (int) gameWorld.developmentSkill.get(id);
            developmentSkillLabel.text.update(String.valueOf(displayedDevelopmentSkill));
            final int displayedMaintenanceSkill = (int) gameWorld.maintenanceSkill.get(id);
            maintenanceSkillLabel.text.update(String.valueOf(displayedMaintenanceSkill));
        }
    }

    final class TaskSelector extends Button {
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
                            Task assignedTask = ((TaskItem) menuItem).task;
                            gameWorld.tasked.set(worker.id, assignedTask);
                            worker.didChange();
                        }
                    };
                }

                private Menu createMenu() {
                    Menu menu = new Menu(AxisLayout.vertical().offStretch().gap(3));
                    for (Task task : CoreTask.VALUES) {
                        menu.add(new TaskItem(task));
                    }
                    return menu;
                }
            });
        }

        private void setTextBasedOnCurrentTaskOf(Entity worker) {
            final Task currentTask = gameWorld.tasked.get(worker.id);
            String taskName = currentTask.name();
            text.update(taskName + " " + DOWN_ARROW);
        }
    }

    private static final class TaskItem extends MenuItem {

        public final Task task;

        public TaskItem(Task task) {
            super(task.name());
            this.task = checkNotNull(task);
        }
    }

}
