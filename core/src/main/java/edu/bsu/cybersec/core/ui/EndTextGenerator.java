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

package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.DecimalTruncator;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.Goal;

public class EndTextGenerator {
    private final int finalUsers;
    private final float finalExpsoure;
    private final Goal goal;
    private final DecimalTruncator truncator = new DecimalTruncator(2);

    EndTextGenerator(GameWorld world) {
        finalUsers = world.users.get().intValue();
        finalExpsoure = world.exposure.get();
        goal = world.company.get().goal;
    }

    String generatorEndText() {
        if (goal.isMet(finalUsers, finalExpsoure)) {
            return "We needed " + goal.minimumUsers + " and less than "
                    + truncator.makeTruncatedString(goal.maximumExposure * 100)
                    + "% exposure. You made some great choices! I look forward to working with you.";
        } else if (!goal.isAcceptableExposure(finalExpsoure) && !goal.isEnoughUsers(finalUsers)) {
            return "Not only did we have fewer than " + goal.minimumUsers + " users, but our exposure was also above "
                    + truncator.makeTruncatedString(goal.maximumExposure * 100) + "%! Your decisions were awful. You're fired.";
        } else if (!goal.isAcceptableExposure(finalExpsoure)) {
            return "We needed " + goal.minimumUsers + " and less than "
                    + truncator.makeTruncatedString(goal.maximumExposure * 100)
                    + "% exposure. You neglected your duties as a security professional.";
        } else {
            return "We needed " + goal.minimumUsers + " and less than "
                    + truncator.makeTruncatedString(goal.maximumExposure * 100)
                    + "% exposure. You did well with security, but that stopped us from gaining enough users.";
        }
    }
}
