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

public class DDOSEvent extends NarrativeEvent {
    private int HOURS_UNTIL_NOTIFY = 0;
    private final float PERCENT_LOSS_ON_IGNORE = .10f;

    public DDOSEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        return "An angry customer is using a Distributed Denial of Service attack to overload your server capacity. Your servers are now down, and the server team is working hard to get them back up. What should you do?";
    }

    @Override
    public List<? extends Option> options() {
        return ImmutableList.of(new PressReleaseOption(), new IgnoreOption());
    }

    @Override
    public void run() {
        super.run();
        ((GameWorld.Systematized) world).userGenerationSystem.userGenerationEnabled.update(false);
        HOURS_UNTIL_NOTIFY = (int) (world.exposure.get() * 100f);
    }

    private class PressReleaseOption extends Option.Terminal {
        private final String text = "Press Release";

        public PressReleaseOption() {
            setLogMessage(DDOSEvent.class.getCanonicalName() + ": " + text);
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            after(HOURS_UNTIL_NOTIFY).post(new NarrativeEvent(world) {
                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new DoNothingOption("Ok"));
                }

                @Override
                public String text() {
                    return "You gave a press conference about the incident and how the company is working to get servers back up. You lost no users, and, after " + HOURS_UNTIL_NOTIFY + " hours, your servers are back online!";
                }

                @Override
                public void run() {
                    ((GameWorld.Systematized) world).userGenerationSystem.userGenerationEnabled.update(true);
                    super.run();
                }
            });
        }
    }

    private class IgnoreOption extends Option.Terminal {
        private final String text = "Just Wait";

        public IgnoreOption() {
            setLogMessage(DDOSEvent.class.getCanonicalName() + ": " + text);
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            after(HOURS_UNTIL_NOTIFY).post(new AbstractUserLossEvent(world, PERCENT_LOSS_ON_IGNORE) {
                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new DoNothingOption("Ok"));
                }

                @Override
                public String text() {
                    return "After " + HOURS_UNTIL_NOTIFY + " hours, your servers are back up! Unfortunately, " + loss + " users have left because they got frustrated about your servers being down for no reason.";
                }

                @Override
                public void run() {
                    ((GameWorld.Systematized) world).userGenerationSystem.userGenerationEnabled.update(true);
                    super.run();
                }
            });
        }
    }
}

