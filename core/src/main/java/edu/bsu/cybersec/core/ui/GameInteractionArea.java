package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Image;
import pythagoras.f.IDimension;
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
    private Group shown = new Group(AxisLayout.vertical().offStretch())
            .setConstraint(AxisLayout.stretched())
            .addStyles(Style.BACKGROUND.is(Background.solid(Colors.RED)));
    private final InteractionAreaGroup statusGroup;

    public GameInteractionArea(GameWorld gameWorld, Interface iface) {
        super(AxisLayout.vertical().offStretch());
        this.gameWorld = checkNotNull(gameWorld);
        this.iface = checkNotNull(iface);
        statusGroup = new CompanyStatusGroup(gameWorld);

        showDefaultView();
        add(shown);
        add(makeButtonArea().setConstraint(AxisLayout.fixed()));
        addStyles(Style.BACKGROUND.is(Background.solid(Colors.MAGENTA)));
    }

    private void showDefaultView() {
        shown.add(statusGroup);
    }

    private Element makeButtonArea() {
        return new Group(AxisLayout.horizontal())
                .add(new ChangeViewButton("dollar-sign.png", statusGroup),
                        new ChangeViewButton("star.png", new FeatureGroup(gameWorld)),
                        new ChangeViewButton("wrench.png", new DefectsGroup()),
                        new ChangeViewButton("envelope.png", new EventsGroup(gameWorld)));
    }

    private final class ChangeViewButton extends Button {
        private static final float PERCENT_OF_VIEW_HEIGHT = 0.06f;
        private static final float FLASH_PERIOD = 300f;

        //this come from triplePlay simpeStyles class: https://github.com/threerings/tripleplay/blob/master/core/src/main/java/tripleplay/ui/SimpleStyles.java#L27
        //Once we make our own styles, we can replace this.
        private final Background CALLOUT_BACKGROUND = Background.roundRect(SimGame.game.plat.graphics(),
                Colors.BLACK, 5, 0xFFEEEEEE, 2).inset(5, 6, 2, 6);
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

        private final Runnable calloutThemer = new Runnable() {
            @Override
            public void run() {
                setStyles(Style.BACKGROUND.is(CALLOUT_BACKGROUND));
            }
        };

        private final Runnable regularThemer = new Runnable() {
            @Override
            public void run() {
                setStyles(Style.BACKGROUND.is(REGULAR_BACKGROUND));
            }
        };

        private Animation.Handle animationHandle;

        ChangeViewButton(String fileName, final InteractionAreaGroup view) {
            super("");
            final Icon icon = makeIconFromImage("images/" + fileName);
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
            group.action(calloutThemer)
                    .then()
                    .delay(FLASH_PERIOD)
                    .then()
                    .action(regularThemer)
                    .then()
                    .delay(FLASH_PERIOD);
            return group.toAnim();
        }

        private Icon makeIconFromImage(String path) {
            final Image iconImage = SimGame.game.plat.assets().getImageSync(path);
            final IDimension viewSize = SimGame.game.plat.graphics().viewSize;
            final float desiredHeight = viewSize.height() * PERCENT_OF_VIEW_HEIGHT;
            final float scale = desiredHeight / iconImage.height();
            return Icons.scaled(Icons.image(iconImage), scale);
        }

    }

    final class DefectsGroup extends InteractionAreaGroup {
        public DefectsGroup() {
            super(AxisLayout.horizontal());
            add(new Label("Defects"));
        }
    }
}




