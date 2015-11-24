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


import com.google.common.collect.Collections2;
import tripleplay.entity.Entity;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class NarrativeEvent implements Runnable {

    public interface Option {
        String text();

        void onSelected();

        final class DoNothingOption implements Option {
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
        }

        final class OkOption implements Option {

            @Override
            public String text() {
                return "OK";
            }

            @Override
            public final void onSelected() {
                // Do nothing
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
        return Collections2.filter(world.workers, new AvailablePredicate(world));
    }
}
