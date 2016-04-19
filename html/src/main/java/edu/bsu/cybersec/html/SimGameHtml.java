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

import com.google.gwt.core.client.EntryPoint;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.TrackedEvent;
import playn.html.HtmlPlatform;
import react.Slot;

public class SimGameHtml implements EntryPoint {

    @Override
    public void onModuleLoad() {
        HtmlPlatform.Config config = new HtmlPlatform.Config();
        HtmlPlatform plat = new HtmlPlatform(config);
        plat.assets().setPathPrefix("sim/");
        SimGame game = new SimGame(plat, new HtmlGameConfig());
        game.event.connect(new Slot<TrackedEvent>() {
            @Override
            public void onEmit(TrackedEvent event) {
                sendTrackingEvent(event);
            }
        });
        plat.start();
    }

    private static native void sendTrackingEvent(TrackedEvent event) /*-{
           $wnd.ga('send', {
              hitType: 'event',
              eventCategory: event.@edu.bsu.cybersec.core.TrackedEvent::category,
              eventAction:  event.@edu.bsu.cybersec.core.TrackedEvent::action,
              eventLabel: event.@edu.bsu.cybersec.core.TrackedEvent::label
            });
        }-*/;
}
