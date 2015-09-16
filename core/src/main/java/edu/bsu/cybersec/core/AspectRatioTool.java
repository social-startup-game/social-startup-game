package edu.bsu.cybersec.core;

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
