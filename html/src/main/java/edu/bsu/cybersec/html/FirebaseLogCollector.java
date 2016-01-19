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

package edu.bsu.cybersec.html;

import playn.core.Log;

public final class FirebaseLogCollector implements Log.Collector {

    // The warnings being suppressed here are false positives, since this field is used in native Javascript.
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String record;

    public FirebaseLogCollector() {
        this.record = createGameRecord();
    }

    private native String createGameRecord()/*-{
       return $wnd.firebaseRef.push();
    }-*/;

    @Override
    public void logged(Log.Level level, String msg, Throwable e) {
        if (level == Log.Level.INFO) {
            log(msg);
        }
    }

    private native void log(String message)/*-{
       var record = this.@edu.bsu.cybersec.html.FirebaseLogCollector::record;
       record.push(
         {
           'timestamp' : new Date().getTime(),
           'message' : message
         }
       );
     }-*/;
}
