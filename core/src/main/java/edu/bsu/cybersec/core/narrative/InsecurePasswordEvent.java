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
import edu.bsu.cybersec.core.RandomInRange;

import java.util.List;

public class InsecurePasswordEvent extends NarrativeEvent {
    private float PERCENT_LOSS = 0.05f;

    public InsecurePasswordEvent(GameWorld world) {
        super(world);
    }

    @Override
    public List<? extends Option> options() {
        return ImmutableList.of(new TrainingOption(), new IgnoreOption());
    }

    @Override
    public String text() {
        return "Some private information was stolen because one of the employees in advertising was using a weak password. " +
                "What would you like to do?";
    }

    class TrainingOption extends Option.Terminal {
        private final String text = "Train staff on secure passwords";
        private int HOURS_UNTIL_NOTIFY_ON_TRAIN = 8;

        public TrainingOption() {
            setLogMessage(InsecurePasswordEvent.class.getCanonicalName() + ": " + text);
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            after(HOURS_UNTIL_NOTIFY_ON_TRAIN).post(new AbstractUserLossEvent(world, PERCENT_LOSS) {
                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new DoNothingOption("Ok"));
                }

                @Override
                public String text() {
                    return loss + " users have left our service after hearing about someone using a weak password. " +
                            "\n\nThankfully, we took the time to train our staff, and this won't be a problem again!";
                }
            });
        }
    }

    class IgnoreOption extends Option.Terminal {
        private final String text = "Just change his password";
        private final int HOURS_UNTIL_NOTIFY_ON_CHANGE_PASSWORD = 2;

        public IgnoreOption() {
            setLogMessage(InsecurePasswordEvent.class.getCanonicalName() + ": " + text);
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            after(HOURS_UNTIL_NOTIFY_ON_CHANGE_PASSWORD).post(new AbstractUserLossEvent(world, PERCENT_LOSS) {
                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new DoNothingOption("Ok"));
                }

                @Override
                public String text() {
                    return loss + " users have left our service after after hearing about someone using a weak password." +
                            "\n\nHopefully, this doesn't happen again...";
                }
            });
            after(new RandomInRange(8, 48).nextInt()).post(new InsecurePasswordEvent(world));
        }

    }
}
