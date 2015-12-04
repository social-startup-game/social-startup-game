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
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;

import java.util.List;

public class DDOSEvent extends NarrativeEvent {
    private static final int HOURS_UNTIL_NOTIFY = 10;
    private static final float PERCENT_LOSS_ON_IGNORE = .10f;

    public DDOSEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        return "Somewhere along the way, a tech savvy customer became disgruntled about his selfie not winning Selfie of the Week. He used a Distributed Denial of Service attack to overload your server capacity. Your servers are now down, and tech support is working to get it back up. What should you do?";
    }

    @Override
    public List<? extends Option> options() {
        return ImmutableList.of(new PressReleaseOption(), new IgnoreOption());
    }

    @Override
    public void run() {
        world.onServerDownEvent.emit();
        super.run();
    }

    private class PressReleaseOption extends Option.Terminal {
        @Override
        public String text() {
            return "PressRelease";
        }

        @Override
        public void onSelected() {
            after(HOURS_UNTIL_NOTIFY).post(new NarrativeEvent(world) {
                @Override
                public String text() {
                    return "You gave a press conference about the incident and how the company is working to get servers back up. You lost no users, and, after " + HOURS_UNTIL_NOTIFY + " hours, your servers are back online!";
                }

                @Override
                public void run() {
                    world.onServerBackUpEvent.emit();
                    super.run();
                }
            });
        }
    }

    private class IgnoreOption extends Option.Terminal {
        @Override
        public String text() {
            return "Just Wait";
        }

        @Override
        public void onSelected() {
            after(HOURS_UNTIL_NOTIFY).post(new AbstractUserLossEvent(world, PERCENT_LOSS_ON_IGNORE) {
                @Override
                public String text() {
                    return "After " + HOURS_UNTIL_NOTIFY + " hours, your servers are back up! Unfortunately, " + loss + " users have left your service because they got frustrated about your servers being down for no reason.";
                }

                @Override
                public void run() {
                    world.onServerBackUpEvent.emit();
                    super.run();
                }
            });
        }
    }
}

