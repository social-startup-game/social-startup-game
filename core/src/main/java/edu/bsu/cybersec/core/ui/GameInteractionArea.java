package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Image;
import pythagoras.f.IDimension;
import react.Slot;
import react.ValueView;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

public final class GameInteractionArea extends Group {

    private final GameWorld gameWorld;
    private Group shown = new Group(AxisLayout.vertical().offStretch())
            .setConstraint(AxisLayout.stretched())
            .addStyles(Style.BACKGROUND.is(Background.solid(Colors.RED)));
    private final CompanyStatusGroupSystem companyStatusGroupSystem;

    public GameInteractionArea(GameWorld gameWorld) {
        super(AxisLayout.vertical().offStretch());
        this.gameWorld = checkNotNull(gameWorld);
        this.companyStatusGroupSystem = new CompanyStatusGroupSystem(gameWorld);

        showDefaultView();
        add(shown);
        add(makeButtonArea().setConstraint(AxisLayout.fixed()));
        addStyles(Style.BACKGROUND.is(Background.solid(Colors.MAGENTA)));
    }

    private void showDefaultView() {
        shown.add(companyStatusGroupSystem.group);
    }

    private Element makeButtonArea() {
        return new Group(AxisLayout.horizontal())
                .add(new ChangeViewButton("dollar-sign.png", companyStatusGroupSystem.group),
                        new ChangeViewButton("star.png", new FeatureGroup()),
                        new ChangeViewButton("wrench.png", new DefectsGroup()),
                        new ChangeViewButton("envelope.png", new EventsGroup(gameWorld)));
    }

    private final class ChangeViewButton extends Button {
        private static final float PERCENT_OF_VIEW_HEIGHT = 0.06f;
        //this come from triplePlay simpeStyles class: https://github.com/threerings/tripleplay/blob/master/core/src/main/java/tripleplay/ui/SimpleStyles.java#L27
        //Once we make our own styles, we can replace this.
        private final Background CALLOUT_BACKGROUND = Background.roundRect(SimGame.game.plat.graphics(),
                Colors.BLACK, 5, 0xFFEEEEEE, 2).inset(5, 6, 2, 6);
        private final Background REGULAR_BACKGROUND = Background.roundRect(SimGame.game.plat.graphics(),
                0xFFCCCCCC, 5, 0xFFEEEEEE, 2).inset(5, 6, 2, 6);

        ChangeViewButton(String fileName, final InteractionAreaGroup view) {
            super("");
            final Icon icon = makeIconFromImage("images/" + fileName);
            super.icon.update(icon);
            view.onAttention().connect(new ValueView.Listener<Boolean>() {
                @Override
                public void onChange(Boolean needsAttention, Boolean oldValue) {
                    if (needsAttention) {
                        setStyles(Style.BACKGROUND.is(CALLOUT_BACKGROUND));
                    } else {
                        setStyles(Style.BACKGROUND.is(REGULAR_BACKGROUND));
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

        private Icon makeIconFromImage(String path) {
            final Image iconImage = SimGame.game.plat.assets().getImageSync(path);
            final IDimension viewSize = SimGame.game.plat.graphics().viewSize;
            final float desiredHeight = viewSize.height() * PERCENT_OF_VIEW_HEIGHT;
            final float scale = desiredHeight / iconImage.height();
            return Icons.scaled(Icons.image(iconImage), scale);
        }

    }
}


final class FeatureGroup extends InteractionAreaGroup {
    public FeatureGroup() {
        super(AxisLayout.horizontal());
        add(new Label("Features"));
    }
}


final class DefectsGroup extends InteractionAreaGroup {
    public DefectsGroup() {
        super(AxisLayout.horizontal());
        add(new Label("Defects"));
    }
}

