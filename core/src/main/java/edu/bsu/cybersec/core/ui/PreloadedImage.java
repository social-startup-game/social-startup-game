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

package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.SimGame;
import playn.core.Image;

public enum PreloadedImage {
    ESTEBAN("Esteban.png"),
    NANCY("Nancy.png"),
    JERRY("Jerry.png"),
    EMPLOYEE_BG("employee_bg.png"),
    ADMIN("admin.png"),
    DOLLAR_SIGN("dollar-sign.png"),
    ENVELOPE("envelope.png"),
    STAR("star.png"),
    WRENCH("wrench.png");

    public final Image image;

    PreloadedImage(String name) {
        this.image = SimGame.game.plat.assets().getImage("images/" + name);
    }
}
