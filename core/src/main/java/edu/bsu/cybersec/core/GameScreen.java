package edu.bsu.cybersec.core;

import playn.core.Clock;
import playn.core.Game;
import react.Slot;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.util.Colors;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class GameScreen extends ScreenStack.UIScreen {
    private static final float SECONDS_PER_HOUR = 60 * 60;
    private GameWorld.Systematized world = new GameWorld.Systematized() {
        @SuppressWarnings("unused")
        private tripleplay.entity.System timeRenderingSystem = new System(this, 0) {

            private final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss aaa");
            private final long START_MS = new GregorianCalendar().getTimeInMillis();
            private final GregorianCalendar now = new GregorianCalendar();

            {
                format.setCalendar(now);
            }

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(gameTime);
            }

            @Override
            protected void update(Clock clock, System.Entities entities) {
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int id = entities.get(i);
                    final int tick = gameTime.get(id);
                    now.setTimeInMillis(START_MS + tick);
                }
                final String formatted = format.format(now.getTime());
                timeLabel.text.update(formatted);
            }
        };

        @SuppressWarnings("unused")
        tripleplay.entity.System hudRenderingSystem = new System(this, 0) {
            @Override
            protected boolean isInterested(Entity entity) {
                return true;
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                int users = userGenerationSystem.users.get().intValue();
                usersLabel.text.update("Users: " + users);
            }
        };

        @SuppressWarnings("unused")
        tripleplay.entity.System progressRenderingSystem = new System(this, 0) {
            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(type)
                        && type.get(entity.id) == Type.FEATURE
                        && entity.has(progress);

            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    int id = entities.get(i);
                    progressLabel.text.update("Progress: " + String.format("%.1f", progress.get(id)));
                }
            }
        };
    };

    private final Label timeLabel = new Label("").addStyles(Style.COLOR.is(Colors.WHITE));
    private final Label usersLabel = new Label("").addStyles(Style.COLOR.is(Colors.WHITE));
    private final Label progressLabel = new Label("").addStyles(Style.COLOR.is(Colors.WHITE));
    private final ToggleButton pauseButton = new ToggleButton("Pause");

    public GameScreen() {
        update.connect(new Slot<Clock>() {
            @Override
            public void onEmit(Clock clock) {
                world.update(clock);
            }
        });
        initializeWorld();
        configurePauseButton();
    }

    private void initializeWorld() {
        makeClock();
        makeExistingFeature();
        makeFeatureInDevelopment();
        makeDeveloper();
    }

    private void makeClock() {
        Entity clock = world.create(true).add(world.type, world.gameTime, world.gameTimeScale);
        final int id = clock.id;
        world.type.set(id, Type.CLOCK);
        world.gameTime.set(id, 0);
        world.gameTimeScale.set(id, SECONDS_PER_HOUR);
    }

    private void makeDeveloper() {
        Entity developer = world.create(true)
                .add(world.developmentSkill, world.tasked);
        world.tasked.set(developer.id, Task.DEVELOPMENT);
        world.developmentSkill.set(developer.id, 5);
    }

    private void makeExistingFeature() {
        Entity featureEntity = world.create(true).add(world.usersPerSecond);
        world.usersPerSecond.set(featureEntity.id, 10);
    }

    private void makeFeatureInDevelopment() {
        Entity featureInDevelopment = world.create(true)
                .add(world.type, world.progress, world.progressRate);
        world.type.set(featureInDevelopment.id, Type.FEATURE);
        world.progress.set(featureInDevelopment.id, 0);
        world.progressRate.set(featureInDevelopment.id, 0);
    }

    private void configurePauseButton() {
        pauseButton.selected().update(false);
        final SystemToggle toggle = new SystemToggle(world.gameTimeSystem, world.userGenerationSystem, world.progressSystem);
        pauseButton.selected().connect(new Slot<Boolean>() {
            @Override
            public void onEmit(Boolean selected) {
                if (selected) {
                    toggle.disable();
                } else {
                    toggle.enable();
                }
            }
        });
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, new AbsoluteLayout(), SimpleStyles.newSheet(game().plat.graphics()));
        root.add(AbsoluteLayout.at(timeLabel, 50, 50));
        root.add(AbsoluteLayout.at(usersLabel, 50, 100));
        root.add(AbsoluteLayout.at(progressLabel, 50, 150));
        root.add(AbsoluteLayout.at(pauseButton, 400, 50));
        root.setSize(size());
        return root;
    }

    @Override
    public Game game() {
        return SimGame.game;
    }

}
