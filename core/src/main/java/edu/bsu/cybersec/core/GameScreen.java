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

public class GameScreen extends ScreenStack.UIScreen {
    private GameWorld world = new GameWorld() {
        {
            createClockEntity();
            new TimeElapseSystem(this);
        }

        tripleplay.entity.System timeRenderingSystem = new System(this, 0) {

            int now;

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(elapsedSimMs);
            }

            @Override
            protected void update(Clock clock, System.Entities entities) {
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    int entityId = entities.get(i);
                    int elapsed = elapsedSimMs.get(entityId);
                    now += elapsed;
                }
                label.text.update("" + now);
            }
        };

        private void createClockEntity() {
            Entity entity = create(true);
            entity.add(elapsedSimMs);
            elapsedSimMs.set(entity.id, 0);
        }
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
