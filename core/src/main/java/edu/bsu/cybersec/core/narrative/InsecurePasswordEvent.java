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

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import edu.bsu.cybersec.core.RandomInRange;
import edu.bsu.cybersec.core.SimGame;

public class InsecurePasswordEvent extends NarrativeEvent {
    private int HOURS_UNTIL_NOTIFY_ON_TRAIN = 8;
    private int HOURS_UNTIL_NOTIFY_ON_CHANGE_PASSWORD = 2;
    private float PERCENT_LOSS = 0.05f;

    public InsecurePasswordEvent(GameWorld world) {
        super(world);
        eventName = "InsecurePasswordEvent";
    }

    @Override
    public String text() {
        return "Some private information was lost because one of the employees in advertising was using bad passwords." +
                "What would you like to do?";
    }

    class TrainingOption implements Option {
        private final String text = "Train staff on secure passwords";

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            SimGame.game.plat.log().info(eventName + ": " + text);
            after(HOURS_UNTIL_NOTIFY_ON_TRAIN).post(new AbstractUserLossEvent(world, PERCENT_LOSS) {
                @Override
                public String text() {
                    return loss + " users have left our service after hearing about how our employee's actions compromised thier private information." +
                            "\n\nThankfully, we took the time to train our staff, and this won't be a problem again!";
                }
            });
        }

        @Override
        public boolean hasSubsequentPage() {
            return false;
        }

        @Override
        public NarrativeEvent subsequentPage() {
            return null;
        }
    }

    class IgnoreOption implements Option {
        private final String text = "Just change his password";

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            SimGame.game.plat.log().info(eventName + ": " + text);
            after(HOURS_UNTIL_NOTIFY_ON_CHANGE_PASSWORD).post(new AbstractUserLossEvent(world, PERCENT_LOSS) {
                @Override
                public String text() {
                    return loss + " users have left our service after hearing about how our employee's actions compromised thier private information." +
                            "\n\nHopefully, this doesn't happen again...";
                }
            });
            after(new RandomInRange(8, 48).nextInt()).post(new InsecurePasswordEvent(world));
        }

        @Override
        public boolean hasSubsequentPage() {
            return false;
        }

        @Override
        public NarrativeEvent subsequentPage() {
            return null;
        }
    }
}
