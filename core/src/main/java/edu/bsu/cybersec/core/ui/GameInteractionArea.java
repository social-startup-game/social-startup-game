package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Graphics;
import playn.core.Image;
import pythagoras.f.IDimension;
import react.Slot;
import tripleplay.entity.Entity;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.FlowLayout;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.*;

public final class GameInteractionArea extends Group {

    private final GameWorld gameWorld;
    private Group shown = new Group(new FlowLayout());
    private final CompanyStatusGroupSystem companyStatusGroupSystem;

    public GameInteractionArea(GameWorld gameWorld) {
        super(AxisLayout.vertical());
        this.gameWorld = checkNotNull(gameWorld);
        this.companyStatusGroupSystem = new CompanyStatusGroupSystem(gameWorld);

        shown.add(companyStatusGroupSystem.group);
        add(shown.setConstraint(AxisLayout.stretched()));
        add(makeButtonArea().setConstraint(AxisLayout.fixed()));
        addStyles(Style.BACKGROUND.is(Background.solid(Colors.MAGENTA)));
    }

    private Element makeButtonArea() {
        return new Group(AxisLayout.horizontal())
                .add(new ChangeViewButton("dollar-sign.png", companyStatusGroupSystem.group),
                        new ChangeViewButton("star.png", new Label("Features")),
                        new ChangeViewButton("wrench.png", new Label("Defects")),
                        makeButtonThatTurnsBlackAfterHalfADay("envelope.png"));
    }

    private ChangeViewButton makeButtonThatTurnsBlackAfterHalfADay(String fileName) {
        final ChangeViewButton button = new ChangeViewButton(fileName, new EventsGroup(gameWorld));
        Entity e = gameWorld.create(true)
                .add(gameWorld.timeTrigger, gameWorld.event);
        gameWorld.timeTrigger.set(e.id, gameWorld.gameTimeMs + 60 * 60 * 12 * 1000);
        gameWorld.event.set(e.id, new Runnable() {
            @Override
            public void run() {
                button.attention();
            }
        });
        return button;
    }

    private final class ChangeViewButton extends Button {
        private static final float PERCENT_OF_VIEW_HEIGHT = 0.06f;

        ChangeViewButton(String fileName, final Element<?> view) {
            super("");
            final Icon icon = makeIconFromImage("images/" + fileName);
            super.icon.update(icon);
            onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button event) {
                    shown.removeAll();
                    shown.add(view);
                }
            });
        }

        private Icon makeIconFromImage(String path) {
            final Image iconImage = SimGame.game.plat.assets().getImageSync(path);
            final IDimension viewSize = SimGame.game.plat.graphics().viewSize;
            final float desiredHeight = viewSize.height() * PERCENT_OF_VIEW_HEIGHT;
            final float scale = desiredHeight / iconImage.height();
            return Icons.scaled(Icons.image(iconImage), scale);
        }

        private void attention() {
            setStyles(Style.BACKGROUND.is(makeCalloutBackground()));
        }

        private Background makeCalloutBackground() {
            Graphics gfx = SimGame.game.plat.graphics();
            //this come from triplePlay simpeStyles class: https://github.com/threerings/tripleplay/blob/master/core/src/main/java/tripleplay/ui/SimpleStyles.java#L27
            //Once we make our own styles, we can replace this.
            int ulColor = 0xFFEEEEEE;
            return Background.roundRect(gfx, Colors.BLACK, 5, ulColor, 2).inset(5, 6, 2, 6);
        }
    }
}
