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
import edu.bsu.cybersec.core.GameTime;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import playn.core.Image;
import react.*;
import tripleplay.entity.Component;
import tripleplay.entity.Entity;
import tripleplay.ui.*;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.util.BoxPoint;

import static com.google.common.base.Preconditions.checkNotNull;

public final class GameInteractionArea extends Group {

    private final GameWorld gameWorld;
    private final Interface iface;
    private Group shown = new Group(AxisLayout.vertical().stretchByDefault().offStretch())
            .setConstraint(AxisLayout.stretched());
    private final Signal<ChangeViewControl> shownChanged = Signal.create();
    private final InteractionAreaGroup statusGroup;

    public GameInteractionArea(GameWorld gameWorld, Interface iface) {
        super(AxisLayout.vertical().offStretch());
        this.gameWorld = checkNotNull(gameWorld);
        this.iface = checkNotNull(iface);
        statusGroup = new CompanyStatusGroup(gameWorld);

        add(shown);
        add(new ButtonArea().setConstraint(AxisLayout.fixed()));
        addStyles(Style.BACKGROUND.is(Background.solid(Palette.BACKGROUND)));
    }

    private final class ButtonArea extends Group {
        private final FeatureGroup featureGroup = new FeatureGroup(gameWorld);
        private final ExploitsGroup exploitsGroup = new ExploitsGroup(gameWorld);
        private final EventsGroup eventsGroup = new EventsGroup(gameWorld);

        private final ChangeViewControl statusButton = new ChangeViewControl(GameAssets.ImageKey.STATUS, "Status", statusGroup, null);
        private final ChangeViewControl featureButton = new ChangeViewControl(GameAssets.ImageKey.DEVELOPMENT, "Features", featureGroup, makeFeatureCounter());

        private final ChangeViewControl exploitsButton = new ChangeViewControl(GameAssets.ImageKey.MAINTENANCE, "Exploits", exploitsGroup, makeExploitCounter());
        private final ChangeViewControl eventsButton = new ChangeViewControl(GameAssets.ImageKey.NEWS, GameAssets.ImageKey.NEWS_ATTENTION, "Alerts", eventsGroup, null);

        private final ImmutableList<ChangeViewControl> allButtons = ImmutableList.of(statusButton, featureButton, exploitsButton, eventsButton);

        ButtonArea() {
            super(AxisLayout.horizontal());
            returnToStatusButtonOnEventCompletion();
            for (ChangeViewControl button : allButtons) {
                add(button);
            }
            add(new MuteCheckBox());
            setDefaultViewTo(statusButton);
            configureTableValidationOnClockTick();
        }

        private void returnToStatusButtonOnEventCompletion() {
            eventsGroup.onEventCompletion().connect(new UnitSlot() {
                @Override
                public void onEmit() {
                    statusButton.click();
                }
            });
        }

        private void setDefaultViewTo(ChangeViewControl button) {
            button.click();
        }

        private void configureTableValidationOnClockTick() {
            gameWorld.gameTime.connect(new Slot<GameTime>() {
                @Override
                public void onEmit(GameTime gameTime) {
                    if (currentViewIs(exploitsGroup)) {
                        exploitsGroup.validate();
                    }
                    if (currentViewIs(featureGroup)) {
                        featureGroup.validate();
                    }
                }
            });
        }

        private class Counter extends tripleplay.entity.System {
            public final Value<Integer> count = Value.create(0);
            private final int modifier;
            private final Component component;

            protected Counter(Component component) {
                this(component, 0);
            }

            protected Counter(Component component, int mod) {
                super(gameWorld, SystemPriority.UI_LEVEL.value);
                this.component = checkNotNull(component);
                this.modifier = mod;
            }

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(component);
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                count.update(entities.size() + modifier);
            }
        }

        private Value<Integer> makeFeatureCounter() {
            return new Counter(gameWorld.featureNumber, -1).count;
        }

        private Value<Integer> makeExploitCounter() {
            return new Counter(gameWorld.exploitNumber).count;
        }
    }

    @Override
    public Group setConstraint(Layout.Constraint constraint) {
        super.setConstraint(constraint);
        ((InteractionAreaGroup) shown.childAt(0)).invalidate();
        return this;
    }


    final class ChangeViewControl extends Group {
        private static final float PERCENT_OF_VIEW_HEIGHT = 0.06f;
        private static final float FLASH_PERIOD = 300f;
        private final ChangeViewButton button;

        ChangeViewControl(GameAssets.ImageKey imageKey, GameAssets.ImageKey attentionKey, final String text, final InteractionAreaGroup view, final Value<Integer> number) {
            super(new AbsoluteLayout());
            this.button = new ChangeViewButton(text);

            final Image iconImage = SimGame.game.assets.getImage(imageKey);
            final Image attentionImage = SimGame.game.assets.getImage(attentionKey);
            final float desiredHeight = percentOfViewHeight(PERCENT_OF_VIEW_HEIGHT);
            final float desiredWidth = SimGame.game.bounds.width() / 5;

            add(AbsoluteLayout.at(button, 0, 0, desiredWidth, desiredHeight));
            if (number != null) {
                final Label numberLabel = new CountLabel(number);
                add(numberLabel.setConstraint(AbsoluteLayout.uniform(BoxPoint.BR)));
            }

            final Icon theIcon = Icons.scaled(Icons.image(iconImage), desiredHeight * 0.85f / iconImage.height());
            final Icon altIcon = Icons.scaled(Icons.image(attentionImage), desiredHeight * 0.85f / attentionImage.height());

            button.icon.update(theIcon);
            button.addStyles(Style.ICON_CUDDLE.on,
                    Style.ICON_GAP.is(-(int) percentOfViewHeight(0.03f)));

            view.onAttention().connect(new Slot<Boolean>() {
                private final Runnable showAltIconIfNotSelected = new Runnable() {
                    @Override
                    public void run() {
                        if (!currentViewIs(view)) {
                            button.icon.update(altIcon);
                            button.text.update(null);
                        }
                    }
                };
                private final Runnable showRegIcon = new Runnable() {
                    @Override
                    public void run() {
                        button.icon.update(theIcon);
                        button.text.update(text);
                    }
                };

                @Override
                public void onEmit(Boolean selected) {
                    if (selected) {
                        flashWhileNeedsAttention();
                    } else {
                        showRegIcon.run();
                    }
                }

                private void flashWhileNeedsAttention() {
                    iface.anim.action(showAltIconIfNotSelected)
                            .then()
                            .delay(FLASH_PERIOD)
                            .then()
                            .action(showRegIcon)
                            .then()
                            .delay(FLASH_PERIOD)
                            .then()
                            .action(new Runnable() {
                                @Override
                                public void run() {
                                    if (view.needsAttention.get()) {
                                        flashWhileNeedsAttention();
                                    }
                                }
                            });
                }
            });

            setConstraint(Constraints.fixedSize(desiredWidth, desiredHeight));
            button.onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button event) {
                    shown.removeAll();
                    shown.add(view);
                    shownChanged.emit(ChangeViewControl.this);
                }
            });
            shownChanged.connect(new Slot<ChangeViewControl>() {
                @Override
                public void onEmit(ChangeViewControl changeViewControl) {
                    ChangeViewControl.this.setEnabled(changeViewControl != ChangeViewControl.this);
                }
            });

        }

        ChangeViewControl(GameAssets.ImageKey imageKey, String text, final InteractionAreaGroup view, Value<Integer> number) {
            this(imageKey, imageKey, text, view, number);
        }

        public void click() {
            button.click();
        }

        protected final class ChangeViewButton extends Button {
            ChangeViewButton(String text) {
                super(text);
            }

            @Override
            protected Class<?> getStyleClass() {
                return ChangeViewButton.class;
            }
        }

        protected final class CountLabel extends Label {
            CountLabel(ValueView<Integer> value) {
                super(value.get().toString());
                value.connect(new Slot<Integer>() {
                    @Override
                    public void onEmit(Integer integer) {
                        text.update(integer.toString());
                        iface.anim.tweenScale(layer)
                                .to(1.2f)
                                .in(250f)
                                .easeInOut()
                                .then()
                                .tweenScale(layer)
                                .to(1f)
                                .in(250f)
                                .easeInOut();
                    }
                });
            }

            @Override
            protected Class<?> getStyleClass() {
                return CountLabel.class;
            }
        }
    }

    private boolean currentViewIs(InteractionAreaGroup view) {
        return shown.childAt(0) == view;
    }

    private static float percentOfViewHeight(float percent) {
        return SimGame.game.bounds.percentOfHeight(percent);
    }
}

final class MuteCheckBox extends Button {
    private static final Image muteImage = SimGame.game.assets.getImage(GameAssets.ImageKey.MUTE);
    private static final Image unmuteImage = SimGame.game.assets.getImage(GameAssets.ImageKey.UNMUTE);
    private final Jukebox jukebox = Jukebox.instance();
    private final Icon muteIcon;
    private final Icon unmuteIcon;

    MuteCheckBox() {
        super();
        final float desiredWidth = SimGame.game.bounds.percentOfHeight(0.03f);
        final float scale = desiredWidth / muteImage.width();
        muteIcon = Icons.scaled(Icons.image(muteImage), scale);
        unmuteIcon = Icons.scaled(Icons.image(unmuteImage), scale);
        updateIcon();
        onClick(new Slot<Button>() {
            @Override
            public void onEmit(Button button) {
                jukebox.toggleMute();
            }
        });
        jukebox.muted.connect(new Slot<Boolean>() {
            @Override
            public void onEmit(Boolean aBoolean) {
                updateIcon();
            }
        });
    }

    private void updateIcon() {
        icon.update(jukebox.muted.get() ? muteIcon : unmuteIcon);
    }
}