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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import tripleplay.entity.Entity;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class NarrativeEvent implements Runnable {

    public static final class Builder {

        public interface Action {
            void runForSelection(Entity e);
        }

        private final GameWorld world;
        private final List<Option> options = Lists.newArrayListWithCapacity(4);
        private String text;

        private Builder(GameWorld world) {
            this.world = checkNotNull(world);
        }

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public OptionBuilder addOption(String text) {
            return new OptionBuilder(text);
        }

        public Builder addEmployeeSelectionsFor(final Action action) {
            for (final Entity e : world.workers) {
                addOption(world.name.get(e.id).shortName).withAction(new Runnable() {
                    @Override
                    public void run() {
                        action.runForSelection(e);
                    }
                });
            }
            return this;
        }

        public NarrativeEvent build() {
            checkState(text != null, "Text was not specified");
            return new NarrativeEvent(this);
        }

        public final class OptionBuilder {
            private final String text;

            public OptionBuilder(String text) {
                this.text = checkNotNull(text);
            }

            public Builder withAction(Runnable runnable) {
                checkNotNull(runnable);
                options.add(new Option(text, runnable));
                return Builder.this;
            }
        }
    }

    public static Builder inWorld(GameWorld world) {
        return new Builder(world);
    }


    public static class Option {
        public final String text;
        public final Runnable action;

        public Option(String text, Runnable action) {
            this.text = text;
            this.action = action;
        }
    }

    private final GameWorld gameWorld;
    public final String text;
    public final ImmutableList<Option> options;

    private NarrativeEvent(Builder builder) {
        this.gameWorld = checkNotNull(builder.world);
        this.text = builder.text;
        this.options = ImmutableList.copyOf(builder.options);
    }

    @Override
    public void run() {
        gameWorld.onNarrativeEvent.emit(this);
    }
}
