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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class EmployeeProfile {

    public static final class Credential {
        private static Builder named(String name) {
            return new Builder(name);
        }

        private static final class Builder {
            private final String name;
            private String provider;

            private Builder(String name) {
                this.name = checkNotNull(name);
            }

            public Credential from(String provider) {
                this.provider = checkNotNull(provider);
                return new Credential(this);
            }
        }

        public final String provider;
        public final String name;

        private Credential(Builder importer) {
            this.provider = importer.provider;
            this.name = importer.name;
        }

    }

    public final String firstName;
    public final String lastName;
    public final ImmutableList<Credential> credentials;
    public final String bio;

    private EmployeeProfile(Builder importer) {
        this.firstName = importer.firstName;
        this.lastName = importer.lastName;
        this.credentials = ImmutableList.copyOf(importer.credentials);
        this.bio = importer.bio;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("credentials", credentials)
                .add("bio", bio)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EmployeeProfile) {
            EmployeeProfile other = (EmployeeProfile) obj;
            return Objects.equals(this.firstName, other.firstName)
                    && Objects.equals(this.lastName, other.lastName)
                    && Objects.equals(this.bio, other.bio)
                    && Objects.equals(this.credentials, other.credentials);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, bio, credentials);
    }

    public static Builder firstName(String firstName) {
        return new Builder(firstName);
    }

    public static final class Builder {
        private String firstName;
        private String lastName;
        private List<Credential> credentials = Lists.newArrayList();
        private String bio;

        public Builder(String firstName) {
            this.firstName = firstName;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public CredentialBuilder withDegree(String name) {
            return new CredentialBuilder(name);
        }

        public EmployeeProfile bio(String bio) {
            this.bio = bio;
            checkAllFieldsSpecified();
            return new EmployeeProfile(this);
        }

        private void checkAllFieldsSpecified() {
            checkNotNull(firstName);
            checkNotNull(lastName);
            checkNotNull(bio);
        }

        public final class CredentialBuilder {
            private String name;

            private CredentialBuilder(String name) {
                this.name = checkNotNull(name);
            }

            public Builder from(String institution) {
                Credential credential = Credential.named(name).from(institution);
                credentials.add(credential);
                return Builder.this;
            }
        }
    }
}
