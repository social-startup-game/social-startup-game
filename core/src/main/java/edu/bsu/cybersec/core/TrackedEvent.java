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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TrackedEvent {

    public static Builder survey() {
        return new Builder("survey");
    }

    public static Builder game() {
        return new Builder("game");
    }

    public static final class Builder {
        private final String category;
        private String action;
        private String label;

        private Builder(String category) {
            this.category = checkNotNull(category);
        }

        public Builder action(String action) {
            this.action = checkNotNull(action);
            return this;
        }

        public TrackedEvent label(String label) {
            this.label = checkNotNull(label);
            return new TrackedEvent(this);
        }
    }

    public final String category;
    public final String action;
    public final String label;

    private TrackedEvent(Builder builder) {
        this.category = builder.category;
        this.action = builder.action;
        this.label = builder.label;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("category", category)
                .add("action", action)
                .add("label", label)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackedEvent that = (TrackedEvent) o;
        return Objects.equal(category, that.category) &&
                Objects.equal(action, that.action) &&
                Objects.equal(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(category, action, label);
    }
}
