/*
 * Copyright 2016 Paul Gestwicki
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


import com.google.common.collect.Collections2;
import tripleplay.entity.Entity;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class NarrativeEvent implements Runnable {
    public abstract static class Option {

        public abstract String text();

        public abstract String eventAction();

        public abstract String eventLabel();

        public void onSelected() {
            if (SimGame.game != null) {
                SimGame.game.event.emit(TrackedEvent.game()
                        .action(eventAction())
                        .label(eventLabel()));
            }
        }

        public abstract boolean hasSubsequentPage();

        /**
         * @throws UnsupportedOperationException if {@link #hasSubsequentPage()} is false
         */
        public abstract NarrativeEvent subsequentPage();

        public abstract static class Terminal extends Option {
            @Override
            public final boolean hasSubsequentPage() {
                return false;
            }

            @Override
            public final NarrativeEvent subsequentPage() {
                throw new UnsupportedOperationException("No subsequent event");
            }
        }

        public static final class DoNothingOption extends Terminal {
            private final String text;

            public DoNothingOption(String text) {
                this.text = text;
            }

            @Override
            public String text() {
                return text;
            }

            @Override
            public void onSelected() {
                // Do nothing
            }

            @Override
            public String eventAction() {
                return "Do nothing";
            }

            @Override
            public String eventLabel() {
                return "null";
            }
        }
    }

    protected final GameWorld world;

    public NarrativeEvent(GameWorld world) {
        this.world = checkNotNull(world);
    }

    @Override
    public void run() {
        world.onNarrativeEvent.emit(this);
    }

    public abstract List<? extends Option> options();

    public abstract String text();

    protected final Collection<Entity> availableWorkers() {
        checkState(!world.workers.isEmpty(), "There are no workers.");
        return Collections2.filter(world.workers, new AvailablePredicate(world));
    }

    protected SubsequentEventBuilder after(int hours) {
        return new SubsequentEventBuilder(hours);
    }

    protected class SubsequentEventBuilder {
        private final int hours;

        private SubsequentEventBuilder(int hours) {
            this.hours = hours;
        }

        public void post(NarrativeEvent event) {
            postAfterDelay(event, hours * ClockUtils.SECONDS_PER_HOUR);
        }
    }

    protected void post(NarrativeEvent event) {
        postAfterDelay(event, 0);
    }

    private void postAfterDelay(NarrativeEvent event, int afterSeconds) {
        checkNotNull(event);
        final Entity e = world.create(true).add(world.event, world.timeTrigger);
        world.timeTrigger.set(e.id, world.gameTime.get().now + afterSeconds);
        world.event.set(e.id, event);
    }
}
