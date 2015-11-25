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

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Image;
import react.Slot;
import react.ValueView;
import tripleplay.anim.AnimGroup;
import tripleplay.anim.Animation;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

public final class GameInteractionArea extends Group {

    private final GameWorld gameWorld;
    private final Interface iface;
    private Group shown = new Group(AxisLayout.vertical().stretchByDefault().offStretch())
            .setConstraint(AxisLayout.stretched());
    private final InteractionAreaGroup statusGroup;

    public GameInteractionArea(GameWorld gameWorld, Interface iface) {
        super(AxisLayout.vertical().offStretch());
        this.gameWorld = checkNotNull(gameWorld);
        this.iface = checkNotNull(iface);
        statusGroup = new CompanyStatusGroup(gameWorld);

        showDefaultView();
        add(shown);
        add(makeButtonArea().setConstraint(AxisLayout.fixed()));
        addStyles(Style.BACKGROUND.is(Background.solid(Palette.BACKGROUND)));
    }

    private void showDefaultView() {
        shown.add(statusGroup);
    }

    private Element makeButtonArea() {
        ImageCache images = ImageCache.instance();
        return new Group(AxisLayout.horizontal())
                .add(new ChangeViewButton(images.DOLLAR_SIGN, "Status", statusGroup),
                        new ChangeViewButton(images.STAR, "Features", new FeatureGroup(gameWorld)),
                        new ChangeViewButton(images.WRENCH, "Exploits", new ExploitsGroup(gameWorld)),
                        new ChangeViewButton(images.ENVELOPE, "News & Events", new EventsGroup(gameWorld)));
    }

    private final class ChangeViewButton extends Button {
        private static final float PERCENT_OF_VIEW_HEIGHT = 0.06f;
        private static final float FLASH_PERIOD = 300f;

        //this come from triplePlay simpeStyles class: https://github.com/threerings/tripleplay/blob/master/core/src/main/java/tripleplay/ui/SimpleStyles.java#L27
        //Once we make our own styles, we can replace this.
        private final Background ATTENTION_BACKGROUND = Background.roundRect(SimGame.game.plat.graphics(),
                Colors.BLACK, 5, 0xFFEEEEEE, 2)
                .inset(5, 6, 2, 6);
        private final Background REGULAR_BACKGROUND = Background.roundRect(SimGame.game.plat.graphics(),
                0xFFCCCCCC, 5, 0xFFEEEEEE, 2).inset(5, 6, 2, 6);

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

        private final Runnable attentionThemer = new Runnable() {
            @Override
            public void run() {
                setStyles(Style.BACKGROUND.is(ATTENTION_BACKGROUND));
                addStyles(COMMON_CHANGE_VIEW_BUTTON_STYLES);
            }
        };

        private final Runnable regularThemer = new Runnable() {
            @Override
            public void run() {
                setStyles(Style.BACKGROUND.is(REGULAR_BACKGROUND));
                addStyles(COMMON_CHANGE_VIEW_BUTTON_STYLES);
            }
        };

        private Animation.Handle animationHandle;

        ChangeViewButton(Image iconImage, String text, final InteractionAreaGroup view) {
            super(text);
            addStyles(COMMON_CHANGE_VIEW_BUTTON_STYLES);
            final Icon icon = makeIconFromImage(iconImage);
            super.icon.update(icon);
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
            onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button event) {
                    shown.removeAll();
                    shown.add(view.setConstraint(AxisLayout.stretched()));
                }
            });
        }

        private void loopAnimation() {
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

        private Icon makeIconFromImage(Image iconImage) {
            final float desiredHeight = percentOfViewHeight(PERCENT_OF_VIEW_HEIGHT);
            final float scale = desiredHeight / iconImage.height();
            return Icons.scaled(Icons.image(iconImage), scale);
        }

    }

    private static final Styles COMMON_CHANGE_VIEW_BUTTON_STYLES = Styles.make(Style.ICON_CUDDLE.on,
            Style.ICON_GAP.is(-(int) percentOfViewHeight(0.04f)),
            Style.TEXT_EFFECT.pixelOutline,
            Style.HIGHLIGHT.is(Palette.UNUSED_SPACE),
            Style.COLOR.is(Palette.FOREGROUND));


    private static float percentOfViewHeight(float percent) {
        return percent * SimGame.game.plat.graphics().viewSize.height();
    }
}




