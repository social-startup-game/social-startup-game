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

package edu.bsu.cybersec.core.narrative;

import com.google.common.collect.ImmutableList;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;

import java.util.List;

public final class ChildAdviceEvent extends NarrativeEvent {
    private static final ImmutableList<String> OPTIONS_TEXT = ImmutableList.of(
            "Computer Science",
            "Psychology",
            "Performing Arts",
            "Law");
    private final ImmutableList<Option> options;

    public ChildAdviceEvent(GameWorld world) {
        super(world);
        ImmutableList.Builder<Option> builder = ImmutableList.builder();
        for (String text : OPTIONS_TEXT) {
            builder.add(new MajorOption(text));
        }
        this.options = builder.build();
    }

    @Override
    public String text() {
        return "James from Accounting brought his daughter to work today. She has a question for you! What should she study " +
                "in college to get a job in app development?";
    }

    @Override
    public List<Option> options() {
        return options;
    }

    final class MajorOption extends Option.Terminal {
        String major;

        public MajorOption(String major) {
            this.major = major;
            setLogMessage(ChildAdviceEvent.class.getCanonicalName() + ": " + major);
        }

        @Override
        public String text() {
            return major;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            after(4).post(new NarrativeEvent(world) {
                @Override
                public String text() {
                    return "James from Accounting gives you a call to thank you for speaking with his daughter. " +
                            "He hopes she listens to you and studies " + major + "!";
                }

                @Override
                public void run() {
                    super.run();
                }

                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new DoNothingOption("Ok"));
                }
            });
        }
    }
}
