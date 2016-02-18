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

package edu.bsu.cybersec.core.study.pre;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class SurveyQuestion {

    public static Builder named(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private String logPrefix;
        private final List<Option> options = Lists.newArrayList();

        private Builder(String name) {
            this.name = name;
        }

        public Builder logPrefix(String prefix) {
            this.logPrefix = checkNotNull(prefix);
            return this;
        }

        public Builder option(String text) {
            options.add(new Option(text));
            return this;
        }

        public SurveyQuestion build() {
            checkState(options.size() >= 2, "Must have at least two options");
            if (logPrefix == null) {
                throw new IllegalStateException("No log prefix");
            }
            return new SurveyQuestion(this);
        }
    }

    public final String text;
    public final String logPrefix;
    public final ImmutableList<Option> options;

    private SurveyQuestion(Builder builder) {
        this.text = builder.name;
        this.logPrefix = builder.logPrefix;
        this.options = ImmutableList.copyOf(builder.options);
    }

    public static final class Option {
        public final String text;

        public Option(String text) {
            this.text = text;
        }
    }
}
