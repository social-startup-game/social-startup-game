package edu.bsu.cybersec.core;

import playn.scene.GroupLayer;

import static com.google.common.base.Preconditions.checkArgument;

public final class AspectRatioTool {

    private final float desiredAspectRatio;

    public AspectRatioTool(float desiredAspectRatio) {
        checkArgument(desiredAspectRatio > 0, "Aspect ratio must be positive");
        this.desiredAspectRatio = desiredAspectRatio;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public GroupLayer createLayer(GroupLayer parent) {
        final float parentWidth = parent.width();
        final float parentHeight = parent.height();
        final float viewAspectRatio = parentWidth / parentHeight;
        GroupLayer child;
        if (desiredAspectRatio < viewAspectRatio) {
            final float childHeight = parentHeight;
            final float childWidth = desiredAspectRatio * childHeight;
            child = new GroupLayer(childWidth, childHeight);
            final float xDiff = parentWidth - childWidth;
            child.setTx(xDiff / 2);
        } else {
            final float childWidth = parentWidth;
            final float childHeight = parentWidth / desiredAspectRatio;
            child = new GroupLayer(childWidth, childHeight);
            final float yDiff = parentHeight - childHeight;
            child.setTy(yDiff / 2);
        }
        return child;
    }


}
