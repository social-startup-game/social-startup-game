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

package edu.bsu.cybersec.android;

import android.annotation.SuppressLint;
import edu.bsu.cybersec.core.GameConfig;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.ui.PlatformSpecificDateFormatter;
import playn.android.GameActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimGameActivity extends GameActivity {

    @Override
    public void main() {
        new SimGame(platform(), new AndroidGameConfig());
    }

    private final class AndroidGameConfig implements GameConfig {
        @Override
        public PlatformSpecificDateFormatter dateFormatter() {
            return new AndroidDateFormatter();
        }

        @Override
        public boolean skipIntro() {
            return false;
        }
    }

    @Override
    protected boolean usePortraitOrientation() {
        return true;
    }
}

class AndroidDateFormatter implements PlatformSpecificDateFormatter {

    @Override
    public String format(long ms) {
        // We are forcing a specific style here to fit into the screen.
        // Maybe someday we will do localization, but probably not.
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat fmt = new SimpleDateFormat(FORMAT_STRING);
        Date date = new Date(ms);
        return fmt.format(date);
    }
}