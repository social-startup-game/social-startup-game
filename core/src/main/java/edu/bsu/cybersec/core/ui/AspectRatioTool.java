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

import pythagoras.f.IDimension;
import pythagoras.f.Rectangle;

import static com.google.common.base.Preconditions.checkArgument;

public final class AspectRatioTool {

    private final float desiredAspectRatio;

    public AspectRatioTool(float desiredAspectRatio) {
        checkArgument(desiredAspectRatio > 0, "Aspect ratio must be positive");
        this.desiredAspectRatio = desiredAspectRatio;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public Rectangle createBoundingBoxWithin(IDimension parent) {
        final float parentWidth = parent.width();
        final float parentHeight = parent.height();
        final float viewAspectRatio = parentWidth / parentHeight;
        if (desiredAspectRatio < viewAspectRatio) {
            final float childHeight = parentHeight;
            final float childWidth = desiredAspectRatio * childHeight;
            final float xDiff = parentWidth - childWidth;
            return new Rectangle(xDiff / 2, 0, childWidth, childHeight);
        } else {
            final float childWidth = parentWidth;
            final float childHeight = parentWidth / desiredAspectRatio;
            final float yDiff = parentHeight - childHeight;
            return new Rectangle(0, yDiff / 2, childWidth, childHeight);
        }
    }


}
