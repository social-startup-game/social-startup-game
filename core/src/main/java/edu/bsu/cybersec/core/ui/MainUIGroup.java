package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.Task;
import edu.bsu.cybersec.core.TaskFormatter;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MainUIGroup extends Group {
    private static final String DOWN_ARROW = "\u25BC";

    private final Interface iface;
    private final GameWorld gameWorld;
    private final Value<Group> focus = Value.create(null);
    private Group contentGroup;

    private final Image employeeBackground = SimGame.game.plat.assets().getImageSync("images/employee_bg.png");

    public MainUIGroup(final GameWorld gameWorld, final Interface iface) {
        super(AxisLayout.vertical().offStretch().gap(0));
        this.iface = checkNotNull(iface);
        this.gameWorld = checkNotNull(gameWorld);
        setupUIConfigurationSystem();
        animateFocusChanges();
    }

    private void setupUIConfigurationSystem() {
        new tripleplay.entity.System(gameWorld, 0) {
            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(gameWorld.name);
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                checkArgument(entities.size() == 3, "Current UI layout assumes three workers.");
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    int id = entities.get(i);
                    final Image image = SimGame.game.plat.assets().getImageSync(gameWorld.imagePath.get(id));

                    final Group group = new Group(AxisLayout.horizontal().offStretch())
                            .addStyles(Style.BACKGROUND.is(
                                    ExpandableParallaxBackground.foreground(image).background(employeeBackground.tile())))
                            .setConstraint(AxisLayout.stretched());

                    Label label = new ClickableLabel("")
                            .onClick(new Slot<ClickableLabel>() {
                                @Override
                                public void onEmit(ClickableLabel event) {
                                    if (focus.get() != group) {
                                        focus.update(group);
                                    } else {
                                        focus.update(null);
                                    }
                                }
                            })
                            .addStyles(Style.COLOR.is(Colors.WHITE))
                            .setConstraint(AxisLayout.stretched(1f));
                    final String name = gameWorld.name.get(id);
                    group.add(label,
                            new Group(AxisLayout.vertical())
                                    .add(new Shim(0, percentOfViewHeight(0.05f)),
                                            new Label(name),
                                            new TaskSelector(root(), gameWorld.entity(id)),
                                            new Shim(0, 0).setConstraint(AxisLayout.stretched())
                                    )
                                    .setConstraint(AxisLayout.stretched(0.5f)));
                    add(group);
                }
                contentGroup = new Group(AxisLayout.horizontal())
                        .add(new GameInteractionArea(gameWorld))
                        .setConstraint(AxisLayout.stretched(2));
                add(contentGroup);
                setEnabled(false);
            }

            private float percentOfViewHeight(float v) {
                return SimGame.game.plat.graphics().viewSize.height() * v;
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
                            gameWorld.tasked.set(worker.id, assignedTask);
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
        }

        private void setTextBasedOnCurrentTaskOf(Entity worker) {
            final int currentTask = gameWorld.tasked.get(worker.id);
            String taskName = formatter.format(currentTask);
            text.update(taskName + " " + DOWN_ARROW);
        }
    }

}
