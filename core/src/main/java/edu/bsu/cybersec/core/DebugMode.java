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

import com.google.common.collect.ImmutableMap;
import edu.bsu.cybersec.core.narrative.ChildAdviceEvent;
import edu.bsu.cybersec.core.narrative.InputSanitizationEvent;
import edu.bsu.cybersec.core.narrative.ScriptKiddieAttackEvent;
import edu.bsu.cybersec.core.narrative.SecurityConferenceEvent;
import edu.bsu.cybersec.core.study.PostSurveyScreen;
import playn.core.Key;
import playn.core.Keyboard;
import react.SignalView;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class DebugMode implements SignalView.Listener<Keyboard.Event> {

    private static final int USER_MOD_AMOUNT = 2000;

    private final ImmutableMap.Builder<Key, Runnable> builder = ImmutableMap.builder();

    {
        builder
                .put(Key.E, new Runnable() {
                    @Override
                    public void run() {
                        gameWorld.exploitSystem.addExploit();
                    }
                })
                .put(Key.S, new Runnable() {
                    @Override
                    public void run() {
                        runEvent(new ScriptKiddieAttackEvent(gameWorld));
                    }
                })
                .put(Key.C, new Runnable() {
                    @Override
                    public void run() {
                        runEvent(new SecurityConferenceEvent(gameWorld));
                    }
                })
                .put(Key.I, new Runnable() {
                    @Override
                    public void run() {
                        runEvent(new InputSanitizationEvent(gameWorld));
                    }
                })
                .put(Key.M, new Runnable() {
                    @Override
                    public void run() {
                        runEvent(new ChildAdviceEvent(gameWorld));
                    }
                })
                .put(Key.END, new Runnable() {
                            @Override
                            public void run() {
                                gameWorld.onGameEnd.emit();
                            }
                        }
                )
                .put(Key.K2, new Runnable() {
                    @Override
                    public void run() {
                        final float currentScale = gameWorld.gameTimeSystem.scale().get();
                        gameWorld.gameTimeSystem.scale().update(currentScale * 2f);
                    }
                })
                .put(Key.K1, new Runnable() {
                    @Override
                    public void run() {
                        final float currentScale = gameWorld.gameTimeSystem.scale().get();
                        gameWorld.gameTimeSystem.scale().update(currentScale / 2f);
                    }
                })
                .put(Key.EQUALS, new Runnable() {
                    @Override
                    public void run() {
                        gameWorld.users.update(gameWorld.users.get() + USER_MOD_AMOUNT);
                    }
                })
                .put(Key.MINUS, new Runnable() {
                    @Override
                    public void run() {
                        gameWorld.users.update(gameWorld.users.get() - USER_MOD_AMOUNT);
                    }
                })
                .put(Key.U, new Runnable() {
                    @Override
                    public void run() {
                        SimGame.game.plat.log().info("Jumping to a new screen");
                        SimGame.game.screenStack.push(new PostSurveyScreen(SimGame.game));
                    }
                });
    }

    private final ImmutableMap<Key, ? extends Runnable> actionMap = builder.build();
    private final GameWorld.Systematized gameWorld;

    public DebugMode(GameWorld.Systematized gameWorld) {
        this.gameWorld = checkNotNull(gameWorld);
    }

    private void runEvent(NarrativeEvent event) {
        Entity entity = gameWorld.create(true).add(gameWorld.event, gameWorld.timeTrigger);
        gameWorld.event.set(entity.id, event);
        gameWorld.timeTrigger.set(entity.id, gameWorld.gameTime.get().now);
    }

    @Override
    public void onEmit(Keyboard.Event event) {
        if (event instanceof Keyboard.KeyEvent) {
            Keyboard.KeyEvent keyEvent = (Keyboard.KeyEvent) event;
            if (keyEvent.down && actionMap.containsKey(keyEvent.key)) {
                actionMap.get(keyEvent.key).run();
            }
        }
    }
}
