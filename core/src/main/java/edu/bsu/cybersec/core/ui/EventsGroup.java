package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Graphics;
import react.Slot;
import tripleplay.ui.*;
import tripleplay.ui.bgs.RoundRectBackground;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

public class EventsGroup extends InteractionAreaGroup {
    private static final Graphics graphics = SimGame.game.plat.graphics();
    private static final Background CALLOUT_BACKGROUND = new RoundRectBackground(graphics,
            Colors.WHITE, percentOfScreenWidth(0.05f), Colors.LIGHT_GRAY, percentOfScreenWidth(0.01f));

    private static float percentOfScreenWidth(float percent) {
        return graphics.viewSize.width() * percent;
    }


    private final GameWorld gameWorld;
    private final Label noEventsLabel = new Label("Nothing to see here. Move along.");

    public EventsGroup(GameWorld gameWorld) {
        super(AxisLayout.vertical().offStretch());
        addStyles(Style.BACKGROUND.is(Background.solid(Colors.CYAN)));
        add(noEventsLabel);
        this.gameWorld = gameWorld;
        gameWorld.onNarrativeEvent.connect(new Slot<NarrativeEvent>() {
            @Override
            public void onEmit(NarrativeEvent event) {
                post(event);
            }
        });
    }

    private void post(NarrativeEvent narrativeEvent) {
        needsAttention.update(true);
        ((GameWorld.Systematized) gameWorld).gameTimeSystem.setEnabled(false);
        removeAll();
        Group callout = new Group(AxisLayout.vertical())
                .add(new Label(narrativeEvent.text)
                        .addStyles(Style.TEXT_WRAP.is(true),
                                Style.COLOR.is(Colors.BLACK)))
                .addStyles(Style.BACKGROUND.is(CALLOUT_BACKGROUND))
                .setConstraint(AxisLayout.stretched());
        Group buttonGroup = new Group(AxisLayout.horizontal());
        for (final NarrativeEvent.Option option : narrativeEvent.options) {
            buttonGroup.add(new Button(option.text).onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button button) {
                    option.action.run();
                    ((GameWorld.Systematized) gameWorld).gameTimeSystem.setEnabled(true);
                    removeAll();
                    needsAttention.update(false);
                    add(noEventsLabel);
                }
            }));
        }
        callout.add(buttonGroup.setConstraint(AxisLayout.fixed()));
        add(callout);
    }
}