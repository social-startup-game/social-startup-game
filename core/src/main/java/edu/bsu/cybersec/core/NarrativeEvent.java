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

    public interface Action {
        void runForSelection(Entity e);
    }

    public static final class Builder {

        private final GameWorld world;
        private final List<Option> options = Lists.newArrayListWithCapacity(4);
        private String text;
        private Action selectedWorkerAction;

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
            this.selectedWorkerAction = checkNotNull(action);
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
    private final ImmutableList<Option> fixedOptions;
    private final Action selectedWorkerAction;

    private NarrativeEvent(Builder builder) {
        this.gameWorld = checkNotNull(builder.world);
        this.text = builder.text;
        this.fixedOptions = ImmutableList.copyOf(builder.options);
        this.selectedWorkerAction = builder.selectedWorkerAction;
    }

    public ImmutableList<Option> options() {
        List<Option> options = Lists.newArrayListWithCapacity(fixedOptions.size() + gameWorld.workers.size());
        if (selectedWorkerAction != null) {
            addWorkersTo(options);
        }
        options.addAll(fixedOptions);
        return ImmutableList.copyOf(options);
    }

    private void addWorkersTo(List<Option> options) {
        for (final Entity e : gameWorld.workers) {
            checkState(e.has(gameWorld.tasked), "Worker is missing its task!");
            if (gameWorld.tasked.get(e.id).isReassignable()) {
                final String name = gameWorld.name.get(e.id).shortName;
                Option option = new Option(name, new Runnable() {
                    @Override
                    public void run() {
                        selectedWorkerAction.runForSelection(e);
                    }
                });
                options.add(option);
            }
        }
    }

    @Override
    public void run() {
        gameWorld.onNarrativeEvent.emit(this);
    }
}
