package edu.bsu.cybersec.core;

import playn.core.Clock;
import playn.core.Game;
import react.Slot;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.game.ScreenStack;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.util.Colors;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class GameScreen extends ScreenStack.UIScreen {
    private GameWorld world = new GameWorld.Systematized() {
        tripleplay.entity.System timeRenderingSystem = new System(this, 0) {

            private final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss aaa");
            private final long START_MS = new GregorianCalendar().getTimeInMillis();
            private final GregorianCalendar now = new GregorianCalendar();

            {
                format.setCalendar(now);
            }

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(simClock);
            }

            @Override
            protected void update(Clock clock, System.Entities entities) {
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int entityId = entities.get(i);
                    final int tick = simClock.get(entityId).tickMS;
                    now.setTimeInMillis(START_MS + tick);
                }
                final String formatted = format.format(now.getTime());
                label.text.update(formatted);
            }
        };


    };

    private final Label label = new Label("").addStyles(Style.COLOR.is(Colors.WHITE));

    public GameScreen() {
        update.connect(new Slot<Clock>() {
            @Override
            public void onEmit(Clock clock) {
                world.update(clock);
            }
        });
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, new AbsoluteLayout(), SimpleStyles.newSheet(game().plat.graphics()));
        root.add(AbsoluteLayout.at(label, 50, 50));
        return root;
    }

    @Override
    public Game game() {
        return SimGame.game;
    }

}
