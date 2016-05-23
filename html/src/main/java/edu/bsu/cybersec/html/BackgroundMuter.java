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

import edu.bsu.cybersec.core.ui.Jukebox;

final class BackgroundMuter {

    void muteMusicWhileDocumentIsHidden() {
        configureVisibilityChangeEventMonitoring(this);
    }

    public native void configureVisibilityChangeEventMonitoring(BackgroundMuter instance)/*-{
       $doc.addEventListener("visibilitychange", function() {
         console.log("visibility change, hidden: " + $doc.hidden);
         if ($doc.hidden) {
           instance.@edu.bsu.cybersec.html.BackgroundMuter::onPageHide()();
         } else {
           instance.@edu.bsu.cybersec.html.BackgroundMuter::onPageShow()();
         }
       });
    }-*/;

    private boolean wasMuted = Jukebox.instance().muted.get();

    @SuppressWarnings("unused") // called by native javascript
    private void onPageHide() {
        wasMuted = Jukebox.instance().muted.get();
        Jukebox.instance().muted.update(true);
    }

    @SuppressWarnings("unused") // called by native javascript
    private void onPageShow() {
        Jukebox.instance().muted.update(wasMuted);
    }
}
