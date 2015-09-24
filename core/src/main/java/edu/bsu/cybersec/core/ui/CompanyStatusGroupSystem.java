package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import playn.core.Clock;
import tripleplay.entity.Entity;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class CompanyStatusGroupSystem extends tripleplay.entity.System {

    private final GameWorld gameWorld;
    public final Group group;
    private final Label progressLabel = new Label("");

    public CompanyStatusGroupSystem(GameWorld world) {
        super(world, 0);
        this.gameWorld = checkNotNull(world);
        group = new Group(AxisLayout.vertical())
                .add(progressLabel);
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(gameWorld.progress);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        final int limit = entities.size();
        checkArgument(limit <= 1, "Expected no more than one progress entity, got " + limit);
        if (limit > 0) {
            final float progress = gameWorld.progress.get(entities.get(0));
            progressLabel.text.update("Progress: " + progress);
        } else {
            progressLabel.text.update("Done.");
        }
    }
}