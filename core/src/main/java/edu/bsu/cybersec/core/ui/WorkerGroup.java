package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.AxisLayoutWeightAnimationValue;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.Task;
import edu.bsu.cybersec.core.TaskFormatter;
import playn.core.Clock;
import react.Slot;
import react.Value;
import react.ValueView;
import tripleplay.entity.Entity;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.util.BoxPoint;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

public class WorkerGroup extends Group {
    private static final String DOWN_ARROW = "\u25BC";

    private final Interface iface;
    private final GameWorld gameWorld;
    private final int[] COLORS = {Colors.ORANGE, Colors.WHITE, Colors.GREEN};
    private final Value<Group> focus = Value.create(null);

    public WorkerGroup(final GameWorld gameWorld, final Interface iface) {
        super(AxisLayout.vertical().offStretch().stretchByDefault().gap(0));
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
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    int id = entities.get(i);
                    String name = gameWorld.name.get(id);
                    final Group group = new Group(AxisLayout.horizontal())
                            .addStyles(Style.BACKGROUND.is(Background.solid(COLORS[i])));
                    Label label = new ClickableLabel(name)
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
                            .addStyles(Style.COLOR.is(Colors.BLACK));
                    group.add(label, new TaskSelector(root(), gameWorld.entity(id)));
                    add(group);
                }
                setEnabled(false);
            }
        };
    }

    private void animateFocusChanges() {
        focus.connect(new ValueView.Listener<Group>() {
            @Override
            public void onChange(Group value, Group oldValue) {
                final float duration = 1000f;
                if (oldValue != null) {
                    iface.anim.tween(new AxisLayoutWeightAnimationValue(oldValue))
                            .from(5).to(1).in(duration).easeOut();
                }
                if (value != null) {
                    iface.anim.tween(new AxisLayoutWeightAnimationValue(value))
                            .from(1).to(5).in(duration).easeOut();
                }
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
            //TODO fix this
            //    interactiveElements.add(this);
        }

        private void setTextBasedOnCurrentTaskOf(Entity worker) {
            final int currentTask = gameWorld.tasked.get(worker.id);
            String taskName = formatter.format(currentTask);
            text.update(taskName + " " + DOWN_ARROW);
        }

    }
}
