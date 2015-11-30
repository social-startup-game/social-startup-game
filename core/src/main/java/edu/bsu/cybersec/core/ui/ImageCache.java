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

import com.google.common.collect.ImmutableList;
import playn.core.Assets;
import playn.core.Image;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class ImageCache {

    private static ImageCache INSTANCE;
    private static Assets assets;
    private ImmutableList.Builder<Image> listBuilder = new ImmutableList.Builder<>();
    public final Image ESTEBAN = load("Esteban.png");
    public final Image NANCY = load("Nancy.png");
    public final Image JERRY = load("Jerry.png");
    public final Image VANI = load("Vani.png");
    public final Image ABDULLAH = load("Abdullah.png");
    public final Image JANINE = load("Janine.png");
    public final Image EMPLOYEE_BG_1 = load("employee_bg_1.jpg");
    public final Image EMPLOYEE_BG_2 = load("employee_bg_2.jpg");
    public final Image EMPLOYEE_BG_4 = load("employee_bg_4.jpg");
    public final Image NARRATIVE_BACKGROUND_1 = load("crop-1.jpg");
    public final Image NARRATIVE_BACKGROUND_2 = load("crop-2.jpg");
    public final Image NARRATIVE_BACKGROUND_3 = load("crop-3.jpg");
    public final Image NARRATIVE_BACKGROUND_4 = load("crop-4.jpg");
    public final Image ADMIN = load("admin.png");
    public final Image DOLLAR_SIGN = load("dollar-sign.png");
    public final Image ENVELOPE = load("envelope.png");
    public final Image DEVELOPMENT = load("development.png");
    public final Image MAINTENANCE = load("maintenance.png");
    public final Image LOGO = load("logo.png");
    public final Image COMPANY_LOGO_WITH_ALPHA = load("company_logo.png");

    private final ImmutableList<Image> all = listBuilder.build();

    private ImageCache() {
    }

    public static ImageCache initialize(Assets assets) {
        if (INSTANCE == null) {
            ImageCache.assets = assets;
            INSTANCE = new ImageCache();
            ImageCache.assets = null;
            return INSTANCE;
        } else {
            return INSTANCE;
        }
    }

    public static void deinitialize() {
        INSTANCE = null;
    }

    public static ImageCache instance() {
        checkState(INSTANCE != null, "Not yet initialized; call initialize first.");
        return INSTANCE;
    }

    private Image load(String name) {
        Image image = assets.getImage("images/" + checkNotNull(name));
        listBuilder.add(image);
        return image;
    }

    public ImmutableList<Image> all() {
        return all;
    }


}
