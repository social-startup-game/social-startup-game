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

package edu.bsu.cybersec.core.narrative;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.EmployeeProfile;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import edu.bsu.cybersec.core.ui.ListStringifier;
import tripleplay.entity.Entity;

import java.util.List;

public class WelcomeEvent extends NarrativeEvent {
    public WelcomeEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        return "Hello! I am Frieda, your administrative assistant.\n\n"
                + "Would you like me to tell you more about your responsibilities?";
    }

    @Override
    public List<? extends Option> options() {
        return ImmutableList.of(
                new Option() {
                    @Override
                    public String text() {
                        return "Yes, please!";
                    }

                    @Override
                    public void onSelected() {
                        // Do nothing
                    }

                    @Override
                    public boolean hasSubsequentPage() {
                        return true;
                    }

                    @Override
                    public NarrativeEvent subsequentPage() {
                        return new TutorialEvent(world);
                    }
                },
                new Option.DoNothingOption("No, thanks! I'm ready!"));
    }
}

final class TutorialEvent extends NarrativeEvent {

    public TutorialEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        ListStringifier stringifier = new ListStringifier();
        return stringifier.stringify(employeeNames())
                + " are currently maintaining our software. You can tap them to find out more about them."
                + "\n\nYou may reassign any number of them to new feature development at any time. Go ahead and try that now, and let me know when you are ready!";
    }

    private List<String> employeeNames() {
        final List<String> list = Lists.newArrayListWithCapacity(3);
        for (int i = 0, limit = world.workers.size(); i < limit; i++) {
            Entity e = world.workers.get(i);
            EmployeeProfile profile = world.profile.get(e.id);
            String name = profile.firstName;
            list.add(name);
        }
        return list;
    }

    @Override
    public List<? extends Option> options() {
        return ImmutableList.of(new Option() {

            @Override
            public String text() {
                return "Got it";
            }

            @Override
            public void onSelected() {
                // Do nothing
            }

            @Override
            public boolean hasSubsequentPage() {
                return true;
            }

            @Override
            public NarrativeEvent subsequentPage() {
                return new NextTutorialEvent(world);
            }
        });
    }
}

final class NextTutorialEvent extends NarrativeEvent {

    public NextTutorialEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        return "Developing features will attract new users. However, each feature also increases our exposure, meaning that it might be easier to be hacked. Keeping some workers maintaining our systems will limit our exposure."
                + "\n\nI think that's everything you need to know. Good luck!";
    }

}