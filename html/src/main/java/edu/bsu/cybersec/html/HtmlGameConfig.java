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

import com.google.gwt.user.client.Window;
import edu.bsu.cybersec.core.GameConfig;
import edu.bsu.cybersec.core.ui.PlatformSpecificDateFormatter;
import playn.html.HtmlPlatform;

import static com.google.common.base.Preconditions.checkNotNull;

public final class HtmlGameConfig extends GameConfig.Default {

    private final GWTDateFormatter formatter = new GWTDateFormatter();
    private final boolean showConsent;
    private final HtmlPlatform plat;

    public HtmlGameConfig(HtmlPlatform plat) {
        this.plat = checkNotNull(plat);
        showConsent = Window.Location.getParameter("consent") != null;
    }

    @Override
    public PlatformSpecificDateFormatter dateFormatter() {
        return formatter;
    }

    @Override
    public boolean showConsentForm() {
        return showConsent;
    }

    @Override
    public void enableGameplayLogging() {
        plat.log().setCollector(new FirebaseLogCollector());
    }
}
