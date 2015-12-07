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

import com.google.common.collect.ImmutableList;
import edu.bsu.cybersec.core.GameTime;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Canvas;
import playn.core.Image;
import playn.core.Surface;
import playn.scene.Layer;
import pythagoras.f.IDimension;
import react.Signal;
import react.Slot;
import react.UnitSlot;
import react.ValueView;
import tripleplay.anim.AnimGroup;
import tripleplay.anim.Animation;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.checkNotNull;

public final class GameInteractionArea extends Group {

    private final GameWorld gameWorld;
    private final Interface iface;
    private Group shown = new Group(AxisLayout.vertical().stretchByDefault().offStretch())
            .setConstraint(AxisLayout.stretched());
    private final Signal<ChangeViewButton> shownChanged = Signal.create();
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

        private final ChangeViewButton statusButton = new ChangeViewButton(GameAssets.ImageKey.STATUS, "Status", statusGroup);
        private final ChangeViewButton featureButton = new ChangeViewButton(GameAssets.ImageKey.DEVELOPMENT, "Features", featureGroup);
        private final ChangeViewButton exploitsButton = new ChangeViewButton(GameAssets.ImageKey.MAINTENANCE, "Exploits", exploitsGroup);
        private final ChangeViewButton eventsButton = new ChangeViewButton(GameAssets.ImageKey.NEWS, GameAssets.ImageKey.NEWS_ATTENTION, "Alerts", eventsGroup);

        private final ImmutableList<ChangeViewButton> allButtons = ImmutableList.of(statusButton, featureButton, exploitsButton, eventsButton);

        ButtonArea() {
            super(AxisLayout.horizontal());
            eventsGroup.onEventCompletion().connect(new UnitSlot() {
                @Override
                public void onEmit() {
                    statusButton.click();
                }
            });
            for (ChangeViewButton button : allButtons) {
                add(button);
            }
            setDefaultViewTo(statusButton);
            configureTableValidationOnClockTick();
        }

        private void setDefaultViewTo(ChangeViewButton button) {
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
    }

    @Override
    public Group setConstraint(Layout.Constraint constraint) {
        super.setConstraint(constraint);
        ((InteractionAreaGroup) shown.childAt(0)).invalidate();
        return this;
    }

    final class ChangeViewButton extends Button {
        private static final float PERCENT_OF_VIEW_HEIGHT = 0.06f;
        private static final float FLASH_PERIOD = 300f;

        private Animation.Handle animationHandle;
        private final InteractionAreaGroup view;
        private boolean drawAttentionBackground = false;

        ChangeViewButton(GameAssets.ImageKey imageKey, GameAssets.ImageKey attentionKey, String text, final InteractionAreaGroup view) {
            super(text);
            this.view = checkNotNull(view);
            final Image iconImage = SimGame.game.assets.getImage(imageKey);
            final Image attentionImage = SimGame.game.assets.getImage(attentionKey);
            final float desiredHeight = percentOfViewHeight(PERCENT_OF_VIEW_HEIGHT);
            final float desiredWidth = SimGame.game.bounds.width() / 5;

            addStyles(Style.BACKGROUND.is(new Background() {
                @Override
                protected Instance instantiate(final IDimension size) {
                    return new LayerInstance(size, new Layer() {
                        private final int DEFAULT_BUTTON_BG_COLOR = 0xFFCCCCCC;
                        private final Canvas canvas = SimGame.game.plat.graphics().createCanvas(size.width(), size.height());
                        private final float radius = percentOfViewHeight(0.008f);
                        private final float radiusOffset = percentOfViewHeight(0.005f);

                        @Override
                        protected void paintImpl(Surface surf) {
                            canvas.setFillColor(Palette.DIALOG_FOREGROUND);
                            canvas.fillRoundRect(0, 0, size.width(), size.height(), radius);
                            if (isSelected()) {
                                canvas.setFillColor(Palette.DIALOG_FOREGROUND);
                            } else if (isEnabled()) {
                                canvas.setFillColor(DEFAULT_BUTTON_BG_COLOR);
                            } else {
                                canvas.setFillColor(Palette.DIALOG_BACKGROUND);
                            }
                            canvas.fillRoundRect(radius - radiusOffset,
                                    radius - radiusOffset,
                                    size.width() - radius,
                                    size.height() - radius,
                                    radius);
                            surf.draw(canvas.toTexture().tile(), 0, 0);

                            final float aspectRatio = iconImage.width() / iconImage.height();
                            final float imageRenderWidth = Math.min(size.width(), size.height() * aspectRatio);
                            final float imageRenderHeight = Math.min(size.height(), size.width() / aspectRatio);
                            surf.draw(drawAttentionBackground ? attentionImage.tile() : iconImage.tile(),
                                    0, 0, imageRenderWidth, imageRenderHeight);
                        }
                    });
                }
            }));
            setConstraint(Constraints.fixedSize(desiredWidth, desiredHeight));
            onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button event) {
                    shown.removeAll();
                    shown.add(view);
                    shownChanged.emit(ChangeViewButton.this);
                }
            });
            shownChanged.connect(new Slot<ChangeViewButton>() {
                @Override
                public void onEmit(ChangeViewButton changeViewButton) {
                    ChangeViewButton.this.setEnabled(changeViewButton != ChangeViewButton.this);
                }
            });
            new AttentionAnimator(view);
        }

        ChangeViewButton(GameAssets.ImageKey imageKey, String text, final InteractionAreaGroup view) {
            this(imageKey, imageKey, text, view);
        }

        private class AttentionAnimator {

            private final Runnable attentionThemer = new Runnable() {
                @Override
                public void run() {
                    drawAttentionBackground = true;
                }
            };

            private final Runnable regularThemer = new Runnable() {
                @Override
                public void run() {
                    drawAttentionBackground = false;
                }
            };

            /**
             * Tag a continued animation onto the end of this one.
             * <p/>
             * It's not clear from the TriplePlay documentation, but you cannot put an Action animation
             * inside of a Repeat animation. The reason for this, as of TP-2.0-rc1, is that Action
             * nulls its reference to its runnable. Hence, to get repeating Action animations (as required
             * here to change label styles), we have to manually append new animations to the end of the
             * current animation when they are done.
             *
             * @see <a href="https://github.com/threerings/tripleplay/blob/master/core/src/main/java/tripleplay/anim/Animation.java">
             * TriplePlay Animation</a>
             */
            private final Runnable animationContinuer = new Runnable() {
                @Override
                public void run() {
                    loopAnimation();
                }
            };

            AttentionAnimator(InteractionAreaGroup view) {
                view.onAttention().connect(new ValueView.Listener<Boolean>() {
                    @Override
                    public void onChange(Boolean needsAttention, Boolean oldValue) {
                        if (needsAttention) {
                            loopAnimation();
                        } else {
                            animationHandle.cancel();
                            regularThemer.run();
                        }
                    }
                });
            }

            private void loopAnimation() {
                if (currentViewIs(view)) {
                    waitBecauseThisViewIsCurrentlyShown();
                } else {
                    flash();
                }
            }

            private void waitBecauseThisViewIsCurrentlyShown() {
                animationHandle = iface.anim.delay(FLASH_PERIOD * 2)
                        .then()
                        .action(animationContinuer)
                        .handle();
            }

            private void flash() {
                animationHandle = iface.anim.add(makeFlashOnceAnimation())
                        .then()
                        .action(animationContinuer)
                        .handle();
            }

            private Animation makeFlashOnceAnimation() {
                AnimGroup group = new AnimGroup();
                group.action(attentionThemer)
                        .then()
                        .delay(FLASH_PERIOD)
                        .then()
                        .action(regularThemer)
                        .then()
                        .delay(FLASH_PERIOD);
                return group.toAnim();
            }
        }

        @Override
        protected Class<?> getStyleClass() {
            return ChangeViewButton.class;
        }
    }

    private boolean currentViewIs(InteractionAreaGroup view) {
        return shown.childAt(0) == view;
    }

    private static float percentOfViewHeight(float percent) {
        return SimGame.game.bounds.percentOfHeight(percent);
    }
}




