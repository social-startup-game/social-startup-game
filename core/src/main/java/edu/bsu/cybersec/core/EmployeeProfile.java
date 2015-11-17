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

import static com.google.common.base.Preconditions.checkNotNull;

public final class EmployeeProfile {

    public enum Degree {
        BS("Bachelor of Science"), MS("Master of Science");
        private final String text;

        Degree(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public final String firstName;
    public final String lastName;
    public final Degree degree;
    public final String university;
    public final String bio;

    private EmployeeProfile(Builder importer) {
        this.firstName = importer.firstName;
        this.lastName = importer.lastName;
        this.degree = importer.degree;
        this.university = importer.university;
        this.bio = importer.bio;
    }

    public static Builder firstName(String firstName) {
        return new Builder(firstName);
    }

    public static final class Builder {
        private String firstName;
        private String lastName;
        private Degree degree;
        private String university;
        private String bio;

        public Builder(String firstName) {
            this.firstName = firstName;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder degree(Degree degree) {
            this.degree = degree;
            return this;
        }

        public Builder university(String university) {
            this.university = university;
            return this;
        }

        public EmployeeProfile bio(String bio) {
            this.bio = bio;
            checkAllFieldsSpecified();
            return new EmployeeProfile(this);
        }

        private void checkAllFieldsSpecified() {
            checkNotNull(firstName);
            checkNotNull(lastName);
            checkNotNull(degree);
            checkNotNull(university);
            checkNotNull(bio);
        }
    }
}
