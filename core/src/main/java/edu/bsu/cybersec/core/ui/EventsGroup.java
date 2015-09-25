package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import react.Slot;
import tripleplay.entity.Entity;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;

public class EventsGroup extends Group {
    private final GameWorld gameworld;
    private final Label noEventsLabel = new Label("Nothing to see here. Move along.");

    public EventsGroup(GameWorld gameWorld) {
        super(AxisLayout.vertical().offStretch());
        add(noEventsLabel);
        this.gameworld = gameWorld;

        Entity e = gameWorld.create(true).add(gameWorld.timeTrigger, gameWorld.event);
        gameWorld.timeTrigger.set(e.id, gameWorld.gameTimeMs + 1000 * 60 * 60 * 5);
        gameWorld.event.set(e.id, new Runnable() {
            @Override
            public void run() {
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
        ((GameWorld.Systematized) gameworld).gameTimeSystem.setEnabled(false);
        removeAll();
        add(new Label(narrativeEvent.text).addStyles(Style.TEXT_WRAP.is(true)));
        Group buttonGroup = new Group(AxisLayout.horizontal());
        for (final Option option : narrativeEvent.options) {
            buttonGroup.add(new Button(option.text).onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button button) {
                    option.action.run();
                    ((GameWorld.Systematized) gameworld).gameTimeSystem.setEnabled(true);
                    removeAll();
                    add(noEventsLabel);
                }
            }));
        }
        add(buttonGroup);
    }
}

class NarrativeEvent {
    public final String text;
    public final Option[] options;

    NarrativeEvent(String text, Option... options) {
        this.text = text;
        this.options = options;
    }
}

class Option {
    public final String text;
    public final Runnable action;

    Option(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }
}
