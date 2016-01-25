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

import edu.bsu.cybersec.core.LogUtils;
import playn.core.Log;

public final class FirebaseLogCollector implements Log.Collector {

    @Override
    public void logged(Log.Level level, String msg, Throwable e) {
        if (level == Log.Level.INFO) {
            if (msg.equals(LogUtils.START_GAME_MESSAGE)) {
                logStartNewGameSession();
            }
            log(msg);
        }
    }

    private native void logStartNewGameSession()/*-{
        $wnd.gameRef = $wnd.firebaseRef.push();
    }-*/;

    private native void log(String message)/*-{
       var target = ($wnd.gameRef === null) ? $wnd.firebaseRef : $wnd.gameRef;
       target.push(
         {
           'timestamp' : new Date().getTime(),
           'message' : message
         }
       );
     }-*/;
}
