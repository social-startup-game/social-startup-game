package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Graphics;
import react.Slot;
import tripleplay.entity.Entity;
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
        initializeSampleEvent();
    }

    private void initializeSampleEvent() {
        final int realTimeSecondsUntilEvent = 2;
        Entity e = gameWorld.create(true).add(gameWorld.timeTrigger, gameWorld.event);
        gameWorld.timeTrigger.set(e.id, gameWorld.gameTimeMs + 1000 * 60 * 60 * realTimeSecondsUntilEvent);
        gameWorld.event.set(e.id, new Runnable() {
            @Override
            public void run() {
                needsAttention.update(true);
                post(new NarrativeEvent("Your workers don't know what they are doing. Train them?",
                        new Option("Yes", new Runnable() {
                            @Override
                            public void run() {
                                SimGame.game.plat.log().debug("Clicked yes");
                            }
                        }),
                        new Option("No", new Runnable() {
                            @Override
                            public void run() {
                                SimGame.game.plat.log().debug("Clicked no");
                            }
                        })));

            }
        });
    }

    private void post(NarrativeEvent narrativeEvent) {
        ((GameWorld.Systematized) gameWorld).gameTimeSystem.setEnabled(false);
        removeAll();
        Group callout = new Group(AxisLayout.vertical())
                .add(new Label(narrativeEvent.text)
                        .addStyles(Style.TEXT_WRAP.is(true),
                                Style.COLOR.is(Colors.BLACK)))
                .addStyles(Style.BACKGROUND.is(CALLOUT_BACKGROUND))
                .setConstraint(AxisLayout.stretched());
        Group buttonGroup = new Group(AxisLayout.horizontal());
        for (final Option option : narrativeEvent.options) {
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

final class NarrativeEvent {
    public final String text;
    public final Option[] options;

    NarrativeEvent(String text, Option... options) {
        this.text = text;
        this.options = options;
    }
}

final class Option {
    public final String text;
    public final Runnable action;

    Option(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }
}
