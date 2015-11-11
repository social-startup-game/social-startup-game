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

package edu.bsu.cybersec.core;

import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultNarrativeScript {

    private GameWorld world;

    public void createIn(GameWorld world, GameConfig config) {
        this.world = checkNotNull(world);
        if (!config.skipWelcome()) {
            new WelcomeEventFactory().addWelcomeEvent();
        }
        new UserDataStolenEventFactory().newEvent();
        new ScriptAttackEventFactory().newEvent();
    }

    private final class WelcomeEventFactory {

        private void addWelcomeEvent() {
            final Entity welcomeEventEntity = world.create(true)
                    .add(world.timeTrigger, world.event);
            world.timeTrigger.set(welcomeEventEntity.id, world.gameTime.get().now);
            world.event.set(welcomeEventEntity.id, makeWelcomeEvent(welcomeEventEntity));
        }

        private Runnable makeWelcomeEvent(final Entity owner) {
            return NarrativeEvent.inWorld(world)
                    .withText("Hello! I am Frieda, your administrative assistant.\n\n" + makeListOfEmployeeNames() + " are currently maintaining our software. You can tap them to find out more about them.\n\nYou may reassign any number of them to new feature development at any time. Go ahead and try that now, and let me know when you are ready!")
                    .addOption("OK").withAction(new EntityRemover(owner))
                    .build();
        }

        private String makeListOfEmployeeNames() {
            final StringBuilder namesBuilder = new StringBuilder();
            final int numberOfWorkers = world.workers.size();
            for (int i = 0, limit = numberOfWorkers - 1; i < limit; i++) {
                final Entity e = world.workers.get(i);
                namesBuilder.append(world.name.get(e.id).shortName);
                namesBuilder.append(", ");
            }
            final Entity last = world.workers.get(numberOfWorkers - 1);
            namesBuilder.append("and ");
            namesBuilder.append(world.name.get(last.id).shortName);
            return namesBuilder.toString();
        }

    }

    private final class UserDataStolenEventFactory {

        private Runnable notifyAction = new Runnable() {
            @Override
            public void run() {
                UserLossAfterNotify loss = new UserLossAfterNotify();
                Entity e = world.create(true).add(world.event, world.timeTrigger);
                world.timeTrigger.set(e.id, world.gameTime.get().now + ClockUtils.SECONDS_PER_HOUR);
                world.event.set(e.id, NarrativeEvent.inWorld(world)
                        .withDynamicText(loss)
                        .addOption("OK")
                        .withAction(loss)
                        .build());
            }

        };

        private final class UserLossAfterNotify implements NarrativeEvent.Text, Runnable {

            private final float percent = 0.20f;
            private float loss;

            @Override
            public String text() {
                loss = world.users.get() * percent;
                return (int) loss + " users are leaving after you notified them of the data breach.";
            }

            @Override
            public void run() {
                world.users.update(world.users.get() - loss);
            }
        }

        private final Runnable ignoreAction = new Runnable() {
            @Override
            public void run() {
                UserLossAfterIgnore loss = new UserLossAfterIgnore();
                Entity e = world.create(true).add(world.event, world.timeTrigger);
                world.timeTrigger.set(e.id, world.gameTime.get().now + ClockUtils.SECONDS_PER_HOUR);
                world.event.set(e.id, NarrativeEvent.inWorld(world)
                        .withDynamicText(loss)
                        .addOption("OK")
                        .withAction(loss)
                        .build());
            }
        };

        private final class UserLossAfterIgnore implements NarrativeEvent.Text, Runnable {
            private final float percent = 0.75f;
            private float loss;

            @Override
            public String text() {
                loss = world.users.get() * percent;
                return "The user community has found out that you failed to disclose a data breach. "
                        + (int) loss + " users angrily leave your service for a competitor!";
            }

            @Override
            public void run() {
                world.users.update(world.users.get() - loss);
            }
        }

        public void newEvent() {
            final Entity e = world.create(true).add(world.event, world.timeTrigger);
            world.event.set(e.id,
                    NarrativeEvent.inWorld(world)
                            .withText("It looks like some of your user data was stolen by hackers! What do you do?")
                            .addOption("Notify our users")
                            .withAction(notifyAction)
                            .addOption("Ignore it")
                            .withAction(ignoreAction)
                            .build());
            world.timeTrigger.set(e.id, world.gameTime.get().now + ClockUtils.SECONDS_PER_HOUR * 4);
        }
    }

    private final class ScriptAttackEventFactory {

        private final int retaliationTime = 12;
        private final int fbiInvestigationTime = 8;

        private void newEvent() {
            final Entity scriptAttackEventEntity = world.create(true).add(world.event, world.timeTrigger);
            world.timeTrigger.set(scriptAttackEventEntity.id, world.gameTime.get().now + ClockUtils.SECONDS_PER_DAY * 2);
            world.event.set(scriptAttackEventEntity.id, makeScriptAttackEvent(scriptAttackEventEntity));
        }

        private Runnable makeScriptAttackEvent(final Entity owner) {
            final UserLossAfterRetaliation loss = new UserLossAfterRetaliation();
            return NarrativeEvent.inWorld(world)
                    .withText("You were attacked by Script Kiddie. They tried and failed to break into your servers via a scripting attack. \n\n Would you like to retaliate? If so, who do you want to assign?")
                    .addEmployeeSelectionsFor(new NarrativeEvent.Action() {
                        @Override
                        public void runForSelection(final Entity worker) {
                            final int returnTime = world.gameTime.get().now + ClockUtils.SECONDS_PER_HOUR * retaliationTime;
                            world.tasked.set(worker.id,
                                    Task.createTask("Retaliating").expiringAt(returnTime).inWorld(world).build());
                            final Entity wakingUp = world.create(true)
                                    .add(world.timeTrigger, world.event);
                            world.timeTrigger.set(wakingUp.id, returnTime);
                            world.event.set(wakingUp.id, new Runnable() {
                                @Override
                                public void run() {
                                    world.tasked.set(worker.id, Task.MAINTENANCE);
                                    wakingUp.close();
                                }
                            });
                            final int investigationTime = world.gameTime.get().now + (ClockUtils.SECONDS_PER_HOUR * (retaliationTime + fbiInvestigationTime));
                            final Entity fbiInvestigationScandal = world.create(true)
                                    .add(world.timeTrigger, world.event);
                            world.timeTrigger.set(fbiInvestigationScandal.id, investigationTime);
                            world.event.set(fbiInvestigationScandal.id, NarrativeEvent.inWorld(world)
                                    .withDynamicText(loss)
                                    .addOption("OK")
                                    .withAction(loss)
                                    .build());
                        }
                    })
                    .addOption("Ignore It").withAction(new EntityRemover(owner))
                    .build();
        }

        private final class UserLossAfterRetaliation implements NarrativeEvent.Text, Runnable {
            private final float percent = 0.75f;
            private float loss;

            @Override
            public String text() {
                loss = world.users.get() * percent;
                return "Not only was having your employee retaliate unsuccessful, it was illegal! The FBI will be looking in to this... \n\n You lost "
                        + (int) loss + " users because of your short temper.";
            }

            @Override
            public void run() {
                world.users.update(world.users.get() - loss);
            }
        }
    }

    private static final class EntityRemover implements Runnable {

        private final Entity e;

        EntityRemover(Entity e) {
            this.e = checkNotNull(e);
        }

        @Override
        public void run() {
            e.close();
        }
    }
}
