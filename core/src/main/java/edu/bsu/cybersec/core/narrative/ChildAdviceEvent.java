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
import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;

import java.util.List;

public class ChildAdviceEvent extends NarrativeEvent {
    List<Option> options = Lists.newArrayList();

    public ChildAdviceEvent(GameWorld world) {
        super(world);
        populateOptionsList();
    }

    private void populateOptionsList() {
        options.add(new majorOption("Computer Science"));
        options.add(new majorOption("Psychology"));
        options.add(new majorOption("Performing Arts"));
        options.add(new majorOption("Law"));
    }

    @Override
    public String text() {
        return "It is take your child to work day! James from accounting brought his daughter. She has a question for you! What should she study " +
                "in order to get a job in app development?";
    }

    @Override
    public List<Option> options() {
        return options;
    }

    final class majorOption extends Option.Terminal {
        String major;

        public majorOption(String major) {
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
                    return "James from accounting would like to thank you for taking the time to answer his duaghter's question." +
                            "He hopes she listens to and studies " + major + "!";
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
